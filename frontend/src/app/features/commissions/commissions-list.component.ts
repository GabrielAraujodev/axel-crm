import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Commission, Page } from '../../core/models/models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-commissions-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Comissões"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhuma comissão encontrada."
      emptyIcon="monetization_on"
      emptyActionLabel="Criar Comissão"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class CommissionsListComponent implements OnInit {
  items: Commission[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  users: any[] = [];
  deals: any[] = [];
  commissionRules: any[] = [];

  columns: ColumnDef[] = [
    { key: 'userName', label: 'Vendedor' },
    { key: 'dealTitle', label: 'Negócio' },
    { key: 'dealValue', label: 'Valor Negócio' },
    { key: 'amount', label: 'Comissão' },
    { key: 'paid', label: 'Pago' },
    { key: 'paidAt', label: 'Data Pagamento' },
  ];

  fields: FieldDef[] = [
    { key: 'userId', label: 'Vendedor', type: 'select', required: true, options: [] },
    { key: 'dealId', label: 'Negócio', type: 'select', required: true, options: [] },
    { key: 'ruleId', label: 'Regra de Comissão', type: 'select', required: true, options: [] },
    { key: 'dealValue', label: 'Valor do Negócio', type: 'number', required: true },
    { key: 'amount', label: 'Valor da Comissão', type: 'number', required: true },
  ];

  constructor(
    private svc: BaseService<Commission>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadRelations();
  }

  loadRelations(): void {
    this.loading = true;
    forkJoin({
      usersPage: this.svc.getPage('users', 0, 1000, 'name,asc'),
      dealsPage: this.svc.getPage('deals', 0, 1000, 'title,asc'),
      rulesPage: this.svc.getPage('commission-rules', 0, 1000, 'name,asc')
    }).subscribe({
      next: (res: any) => {
        this.users = res.usersPage.content;
        this.deals = res.dealsPage.content;
        this.commissionRules = res.rulesPage.content;

        const userField = this.fields.find(f => f.key === 'userId');
        if (userField) userField.options = this.users.map(u => ({ value: u.id, label: u.fullName || u.name }));

        const dealField = this.fields.find(f => f.key === 'dealId');
        if (dealField) dealField.options = this.deals.map(d => ({ value: d.id, label: d.title }));

        const ruleField = this.fields.find(f => f.key === 'ruleId');
        if (ruleField) ruleField.options = this.commissionRules.map(r => ({ value: r.id, label: `${r.name} (${(r.percentage * 100).toFixed(1)}%)` }));

        this.load();
      },
      error: () => this.load()
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('commissions', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Commission>) => {
        this.items = p.content;
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

  openDialog(entity?: Commission): void {
    const formEntity = entity ? {
      userId: entity.userId,
      dealId: entity.dealId,
      ruleId: entity.ruleId,
      dealValue: entity.dealValue,
      amount: entity.amount,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Comissão' : 'Nova Comissão',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('commissions', entity.id!, result)
          : this.svc.create('commissions', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Commission): void {
    if (!confirm('Deseja excluir esta comissão?')) return;
    this.svc.delete('commissions', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluída!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
