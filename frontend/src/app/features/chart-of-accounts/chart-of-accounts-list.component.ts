import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BaseService } from '../../core/services/base.service';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface ChartOfAccount {
  id?: string;
  code: string;
  name: string;
  type: 'RECEITA' | 'DESPESA' | 'ATIVO' | 'PASSIVO';
  parentId?: string | null;
  parentName?: string | null;
  level: number;
}

@Component({
  selector: 'app-chart-of-accounts-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  templateUrl: './chart-of-accounts-list.component.html',
  styleUrls: ['./chart-of-accounts-list.component.scss']
})
export class ChartOfAccountsListComponent implements OnInit {
  dataSource: ChartOfAccount[] = [];
  loading = true;
  uploading = false;

  displayedColumns = ['code', 'name', 'type', 'actions'];

  fields: FieldDef[] = [
    { key: 'code', label: 'Código de Classificação (ex: 1.1.01)', type: 'text', required: true },
    { key: 'name', label: 'Nome da Conta', type: 'text', required: true },
    { key: 'type', label: 'Tipo de Conta', type: 'select', required: true, options: [
      { value: 'RECEITA', label: 'Receita' },
      { value: 'DESPESA', label: 'Despesa' },
      { value: 'ATIVO', label: 'Ativo' },
      { value: 'PASSIVO', label: 'Passivo' }
    ]},
    { key: 'parentId', label: 'Conta Contábil Pai (Opcional)', type: 'select', options: [] }
  ];

  constructor(
    private svc: BaseService<ChartOfAccount>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.http.get<ChartOfAccount[]>(`${environment.apiUrl}/chart-of-accounts`).subscribe({
      next: (data) => {
        this.dataSource = data;
        this.loading = false;

        // Populate parentId options
        const parentField = this.fields.find(f => f.key === 'parentId');
        if (parentField) {
          parentField.options = [
            { value: '', label: 'Nenhuma (Conta de Nível 1)' },
            ...data.map(acc => ({ value: acc.id!, label: `${acc.code} - ${acc.name}` }))
          ];
        }
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Erro ao carregar plano de contas', 'OK', { duration: 3000 });
      }
    });
  }

  openDialog(entity?: ChartOfAccount): void {
    const formEntity = entity ? {
      code: entity.code,
      name: entity.name,
      type: entity.type,
      parentId: entity.parentId || ''
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Conta' : 'Nova Conta Contábil',
      fields: this.fields,
      entity: formEntity
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        if (result.parentId === '') result.parentId = null;

        const op = entity
          ? this.svc.update('chart-of-accounts', entity.id!, result)
          : this.svc.create('chart-of-accounts', result);

        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: (err) => {
            const msg = err.error?.message || 'Erro ao salvar conta contábil';
            this.snackBar.open(msg, 'OK', { duration: 3000 });
          }
        });
      });
  }

  onDelete(entity: ChartOfAccount): void {
    if (!confirm(`Deseja excluir a conta "${entity.name}"?`)) return;
    this.svc.delete('chart-of-accounts', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: (err) => {
        const msg = err.error?.message || 'Erro ao excluir';
        this.snackBar.open(msg, 'OK', { duration: 3000 });
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    const formData = new FormData();
    formData.append('file', file);

    this.uploading = true;
    this.http.post(`${environment.apiUrl}/chart-of-accounts/import`, formData).subscribe({
      next: () => {
        this.uploading = false;
        this.snackBar.open('Plano de Contas importado com sucesso!', 'OK', { duration: 3000 });
        this.load();
      },
      error: (err) => {
        this.uploading = false;
        const msg = err.error?.message || 'Erro ao importar CSV';
        this.snackBar.open(msg, 'OK', { duration: 3000 });
      }
    });
  }
}
