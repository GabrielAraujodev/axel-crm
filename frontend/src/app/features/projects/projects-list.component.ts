import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef, KpiDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Project, Page } from '../../core/models/models';

@Component({
  selector: 'app-projects-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Projetos"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      [kpis]="kpis"
      emptyMessage="Nenhum projeto encontrado."
      emptyIcon="folder"
      emptyActionLabel="Criar Projeto"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="goToDetails($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class ProjectsListComponent implements OnInit {
  items: Project[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  get kpis(): KpiDef[] {
    const totalBudget = this.items.reduce((s, p) => s + (Number(p.budget) || 0), 0);
    const totalCost = this.items.reduce((s, p) => s + (Number(p.cost) || 0), 0);
    return [
      { label: 'Total de Projetos', value: this.totalElements, icon: 'folder', color: 'var(--primary)' },
      { label: 'Orçamento Total', value: totalBudget.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }), icon: 'attach_money', color: '#22c55e' },
      { label: 'Custo Total', value: totalCost.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }), icon: 'receipt', color: '#f59e0b' },
    ];
  }

  clients: any[] = [];
  users: any[] = [];

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'clientName', label: 'Cliente' },
    { key: 'status', label: 'Status' },
    { key: 'startDate', label: 'Início' },
    { key: 'endDate', label: 'Fim' },
    { key: 'budget', label: 'Orçamento' },
    { key: 'cost', label: 'Custo' },
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'startDate', label: 'Início', type: 'date' },
    { key: 'endDate', label: 'Fim', type: 'date' },
    { key: 'budget', label: 'Orçamento', type: 'number' },
    { key: 'cost', label: 'Custo', type: 'number' },
    { key: 'status', label: 'Status', type: 'text' },
    { key: 'clientId', label: 'Cliente', type: 'select', required: true, options: [] },
    { key: 'managerUserId', label: 'Gerente', type: 'select', options: [] },
  ];

  constructor(
    private svc: BaseService<Project>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  goToDetails(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }

  ngOnInit(): void { this.loadRelations(); }

  loadRelations(): void {
    this.loading = true;
    this.svc.getPage('clients', 0, 1000, 'name,asc').subscribe({
      next: (cPage: any) => {
        this.clients = cPage.content;
        const cField = this.fields.find(f => f.key === 'clientId');
        if (cField) cField.options = this.clients.map(c => ({ value: c.id, label: c.name }));

        this.svc.getPage('users', 0, 1000, 'name,asc').subscribe({
          next: (uPage: any) => {
            this.users = uPage.content;
            const uField = this.fields.find(f => f.key === 'managerUserId');
            if (uField) uField.options = this.users.map(u => ({ value: u.id, label: u.fullName || u.name }));
            this.load();
          },
          error: () => this.load()
        });
      },
      error: () => this.load()
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('projects', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Project>) => {
        this.items = p.content.map((item: any) => {
          const client = this.clients.find(c => c.id === item.clientId);
          return {
            ...item,
            clientName: client ? client.name : 'Sem Cliente'
          };
        });
        this.totalElements = p.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });
  }

  onPage(e: PageEvent): void {
    this.page = e.pageIndex;
    this.pageSize = e.pageSize;
    this.load();
  }

  onSort(e: Sort): void {
    this.sort = e.active && e.direction ? `${e.active},${e.direction}` : 'id,asc';
    this.load();
  }

  openDialog(entity?: Project): void {
    const formEntity = entity ? {
      name: entity.name,
      description: entity.description,
      startDate: entity.startDate,
      endDate: entity.endDate,
      budget: entity.budget,
      cost: entity.cost,
      status: entity.status,
      clientId: entity.clientId,
      managerUserId: entity.managerUserId,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Projeto' : 'Novo Projeto',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('projects', entity.id!, result)
          : this.svc.create('projects', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Project): void {
    if (!confirm('Deseja excluir este projeto?')) return;
    this.svc.delete('projects', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
