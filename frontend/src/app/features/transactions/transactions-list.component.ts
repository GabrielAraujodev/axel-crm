import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { FinancialTransaction, Page } from '../../core/models/models';
import { forkJoin } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-transactions-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Transações Financeiras"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhuma transação encontrada."
      emptyIcon="payments"
      emptyActionLabel="Criar Transação"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class TransactionsListComponent implements OnInit {
  items: FinancialTransaction[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  bankAccounts: any[] = [];
  clients: any[] = [];
  deals: any[] = [];

  columns: ColumnDef[] = [
    { key: 'transactionType', label: 'Tipo' },
    { key: 'amount', label: 'Valor' },
    { key: 'transactionDate', label: 'Data' },
    { key: 'chartAccountName', label: 'Conta Contábil' },
    { key: 'description', label: 'Descrição' },
    { key: 'bankAccountName', label: 'Conta Bancária' },
    { key: 'clientName', label: 'Cliente' },
  ];

  fields: FieldDef[] = [
    { key: 'description', label: 'Descrição', type: 'text', required: true },
    { key: 'transactionType', label: 'Tipo', type: 'select', required: true, options: [
      { value: 'INCOME', label: 'Receita' },
      { value: 'EXPENSE', label: 'Despesa' },
      { value: 'TRANSFER', label: 'Transferência' },
      { value: 'REFUND', label: 'Reembolso' },
      { value: 'ADJUSTMENT', label: 'Ajuste' }
    ]},
    { key: 'amount', label: 'Valor', type: 'number', required: true },
    { key: 'transactionDate', label: 'Data da Transação', type: 'date', required: true },
    { key: 'dueDate', label: 'Data de Vencimento', type: 'date' },
    { key: 'paid', label: 'Pago', type: 'checkbox' },
    { key: 'chartAccountId', label: 'Conta Contábil', type: 'select', options: [] },
    { key: 'bankAccountId', label: 'Conta Bancária', type: 'select', required: true, options: [] },
    { key: 'clientId', label: 'Cliente', type: 'select', options: [] },
    { key: 'dealId', label: 'Negócio', type: 'select', options: [] },
  ];

  constructor(
    private svc: BaseService<FinancialTransaction>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadRelations();
  }

  loadRelations(): void {
    this.loading = true;
    forkJoin({
      accountsPage: this.svc.getPage('bank-accounts', 0, 1000, 'name,asc'),
      clientsPage: this.svc.getPage('clients', 0, 1000, 'name,asc'),
      dealsPage: this.svc.getPage('deals', 0, 1000, 'title,asc'),
      chartOfAccounts: this.http.get<any[]>(`${environment.apiUrl}/chart-of-accounts`)
    }).subscribe({
      next: (res: any) => {
        this.bankAccounts = res.accountsPage.content;
        this.clients = res.clientsPage.content;
        this.deals = res.dealsPage.content;

        const accountField = this.fields.find(f => f.key === 'bankAccountId');
        if (accountField) accountField.options = this.bankAccounts.map(a => ({ value: a.id, label: a.name }));

        const clientField = this.fields.find(f => f.key === 'clientId');
        if (clientField) clientField.options = [
          { value: '', label: 'Nenhum' },
          ...this.clients.map(c => ({ value: c.id, label: c.name }))
        ];

        const dealField = this.fields.find(f => f.key === 'dealId');
        if (dealField) dealField.options = [
          { value: '', label: 'Nenhum' },
          ...this.deals.map(d => ({ value: d.id, label: d.title }))
        ];

        const chartField = this.fields.find(f => f.key === 'chartAccountId');
        if (chartField) chartField.options = [
          { value: '', label: 'Nenhum' },
          ...res.chartOfAccounts.map((c: any) => ({ value: c.id, label: `${c.code} - ${c.name}` }))
        ];

        this.load();
      },
      error: () => this.load()
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('financial-transactions', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<FinancialTransaction>) => {
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

  openDialog(entity?: FinancialTransaction): void {
    const formEntity = entity ? {
      description: entity.description,
      transactionType: entity.transactionType,
      amount: entity.amount,
      transactionDate: entity.transactionDate,
      dueDate: entity.dueDate,
      paid: entity.paid,
      chartAccountId: (entity as any).chartAccountId || '',
      bankAccountId: entity.bankAccountId,
      clientId: entity.clientId || '',
      dealId: entity.dealId || '',
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Transação' : 'Nova Transação',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        if (result.clientId === '') result.clientId = null;
        if (result.dealId === '') result.dealId = null;
        if (result.chartAccountId === '') result.chartAccountId = null;

        const op = entity
          ? this.svc.update('financial-transactions', entity.id!, result)
          : this.svc.create('financial-transactions', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: FinancialTransaction): void {
    if (!confirm('Deseja excluir esta transação?')) return;
    this.svc.delete('financial-transactions', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
