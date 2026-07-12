import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { TimelineComponent } from '../../shared/timeline/timeline.component';

import { LeadDetailService } from '../../core/services/lead-detail.service';
import { Lead, Task, User } from '../../core/models/models';

@Component({
  selector: 'app-lead-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatTabsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    FormDialogComponent,
    TimelineComponent
  ],
  templateUrl: './lead-detail.component.html',
  styleUrls: ['./lead-detail.component.scss']
})
export class LeadDetailComponent implements OnInit {
  leadId = '';
  lead: Lead | null = null;
  loading = true;
  saving = false;
  converting = false;
  editMode = false;

  // Forms
  leadForm!: FormGroup;

  // Timeline & History

  // Related data
  tasks: Task[] = [];
  users: User[] = [];

  // Table columns
  taskColumns: string[] = ['title', 'status', 'priority', 'dueDate'];

  // Options for select lists
  sources = [
    { value: 'WEBSITE', label: 'Website' },
    { value: 'SOCIAL_MEDIA', label: 'Redes Sociais' },
    { value: 'REFERRAL', label: 'Indicação' },
    { value: 'EMAIL', label: 'Email' },
    { value: 'PHONE', label: 'Telefone' },
    { value: 'EVENT', label: 'Evento' },
    { value: 'ADVERTISEMENT', label: 'Anúncio' },
    { value: 'PARTNER', label: 'Parceiro' },
    { value: 'OTHER', label: 'Outro' }
  ];

  stages = [
    { value: 'NEW', label: 'Novo' },
    { value: 'CONTACTED', label: 'Contatado' },
    { value: 'QUALIFIED', label: 'Qualificado' },
    { value: 'PROPOSAL', label: 'Proposta' },
    { value: 'NEGOTIATION', label: 'Negociação' },
    { value: 'CONVERTED', label: 'Convertido' },
    { value: 'LOST', label: 'Perdido' },
    { value: 'ARCHIVED', label: 'Arquivado' }
  ];

  taskFields: FieldDef[] = [
    { key: 'title', label: 'Título', type: 'text', required: true },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'status', label: 'Status', type: 'select', required: true, options: [
      { value: 'PENDING', label: 'Pendente' },
      { value: 'IN_PROGRESS', label: 'Em Progresso' },
      { value: 'BLOCKED', label: 'Bloqueado' },
      { value: 'COMPLETED', label: 'Concluído' },
      { value: 'CANCELLED', label: 'Cancelado' }
    ]},
    { key: 'dueDate', label: 'Prazo', type: 'date' },
    { key: 'assignedToUserId', label: 'Responsável', type: 'select', required: true, options: [] }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private service: LeadDetailService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.leadForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      companyName: [''],
      jobTitle: [''],
      source: ['OTHER', Validators.required],
      stage: ['NEW', Validators.required],
      score: [0],
      estimatedValue: [0],
      notes: [''],
      assignedToUserId: [null]
    });
    this.leadForm.disable();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.leadId = params['id'];
      if (this.leadId) {
        this.loadAll();
      }
    });
  }

  loadAll(): void {
    this.loading = true;
    this.service.getById('leads', this.leadId).subscribe({
      next: (data) => {
        this.lead = data;
        this.leadForm.patchValue(data);
        this.leadForm.disable();
        this.loadRelatedData();
        this.loadUsers();
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Erro ao carregar dados do Lead.', 'OK', { duration: 3000 });
        this.loading = false;
        this.router.navigate(['/leads']);
      }
    });
  }

  loadRelatedData(): void {
    this.service.getTasks().subscribe({
      next: (res) => {
        this.tasks = res.content.filter(t => t.leadId === this.leadId);
      }
    });
  }

  loadUsers(): void {
    this.service.getUsers().subscribe({
      next: (res) => {
        this.users = res.content;
        this.taskFields[4].options = this.users.map(u => ({ value: u.id, label: u.fullName || u.name }));
      }
    });
  }

  toggleEditMode(): void {
    if (this.editMode) {
      this.leadForm.patchValue(this.lead!); // Reset changes
      this.leadForm.disable();
    } else {
      this.leadForm.enable();
      // Ensure converted leads can't edit stage to unlock it
      if (this.lead?.stage === 'CONVERTED') {
        this.leadForm.get('stage')?.disable();
      }
    }
    this.editMode = !this.editMode;
  }

  saveLeadDetails(): void {
    if (this.leadForm.invalid) return;
    this.saving = true;
    const body = this.leadForm.getRawValue(); // Handles disabled stage if converted
    this.service.update('leads', this.leadId, body).subscribe({
      next: (updated) => {
        this.lead = updated;
        this.leadForm.patchValue(updated);
        this.leadForm.disable();
        this.editMode = false;
        this.saving = false;
        this.snackBar.open('Lead atualizado com sucesso!', 'OK', { duration: 3000 });
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Erro ao atualizar dados.', 'OK', { duration: 3000 });
      }
    });
  }

  convertLeadToClient(): void {
    if (!confirm('Tem certeza de que deseja converter este Lead em Cliente? Isso criará um cadastro correspondente em Clientes.')) return;
    this.converting = true;
    this.service.convertLead(this.leadId).subscribe({
      next: (newClient) => {
        this.converting = false;
        this.snackBar.open('Lead convertido em Cliente com sucesso!', 'OK', { duration: 3000 });
        
        // Redirect to new client detail page
        this.router.navigate(['/clients', newClient.id]);
      },
      error: () => {
        this.converting = false;
        this.snackBar.open('Erro ao converter Lead.', 'OK', { duration: 3000 });
      }
    });
  }

  openCreateTaskDialog(): void {
    const data: FormDialogData = {
      title: 'Nova Tarefa',
      fields: this.taskFields,
      entity: { status: 'PENDING' }
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const payload = { ...result, leadId: this.leadId };
        this.service.createTask(payload).subscribe({
          next: () => {
            this.snackBar.open('Tarefa criada com sucesso!', 'OK', { duration: 3000 });
            this.loadRelatedData();
          },
          error: () => this.snackBar.open('Erro ao criar tarefa.', 'OK', { duration: 3000 })
        });
      });
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return 'R$ 0,00';
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  formatDate(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getAssignedUserName(userId: string | undefined): string {
    if (!userId) return 'Sem Responsável';
    const found = this.users.find(u => u.id === userId);
    return found ? found.name : 'Carregando...';
  }
}
