import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { BankAccount, Page } from '../../core/models/models';

@Component({
  selector: 'app-bank-accounts-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Contas Bancárias"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhuma conta bancária encontrada."
      emptyIcon="account_balance"
      emptyActionLabel="Criar Conta"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class BankAccountsListComponent implements OnInit {
  items: BankAccount[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'bankName', label: 'Banco' },
    { key: 'accountNumber', label: 'Número' },
    { key: 'agency', label: 'Agência' },
    { key: 'currentBalance', label: 'Saldo Atual' },
    { key: 'active', label: 'Ativo' },
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'bankName', label: 'Banco', type: 'text' },
    { key: 'accountNumber', label: 'Número', type: 'text', required: true },
    { key: 'agency', label: 'Agência', type: 'text' },
    { key: 'currentBalance', label: 'Saldo Inicial', type: 'number' },
    { key: 'active', label: 'Ativo', type: 'checkbox' },
  ];

  constructor(
    private svc: BaseService<BankAccount>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.svc.getPage('bank-accounts', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<BankAccount>) => {
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

  openDialog(entity?: any): void {
    const formEntity = entity ? {
      name: entity.name,
      bankName: entity.bankName,
      accountNumber: entity.accountNumber,
      agency: entity.agency,
      currentBalance: entity.balance !== undefined ? entity.balance : entity.currentBalance,
      active: entity.active,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Conta' : 'Nova Conta',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('bank-accounts', entity.id!, result)
          : this.svc.create('bank-accounts', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: BankAccount): void {
    if (!confirm('Deseja excluir esta conta bancária?')) return;
    this.svc.delete('bank-accounts', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
