import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Invoice, Page } from '../../core/models/models';

@Component({
  selector: 'app-invoices-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="list-page p-6">
      <div class="list-header flex justify-between items-center mb-6">
        <h1 class="page-title text-2xl font-bold text-[#00072d] m-0">Faturamento</h1>
        <div class="flex items-center gap-3">
          <button mat-stroked-button color="accent" class="rounded-xl font-semibold" (click)="downloadReportPdf()" matTooltip="Exportar Relatório Consolidado (PDF)">
            <mat-icon class="text-red-600">picture_as_pdf</mat-icon>
            Exportar Geral (PDF)
          </button>
          <button mat-raised-button color="primary" class="rounded-xl font-semibold" (click)="openDialog()">
            <mat-icon>add</mat-icon>
            Nova Fatura
          </button>
        </div>
      </div>

      @if (loading) {
        <div class="loading-container flex justify-center py-20">
          <mat-spinner diameter="40"></mat-spinner>
        </div>
      } @else {
        <div class="table-container bg-white rounded-2xl border border-gray-100 overflow-hidden shadow-sm">
          <table mat-table [dataSource]="items" matSort (matSortChange)="onSort($event)" class="w-full">
            
            <!-- Invoice Number Column -->
            <ng-container matColumnDef="invoiceNumber">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase">Nº Fatura</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 font-semibold text-blue-600 text-sm">
                {{ row.invoiceNumber || 'N/A' }}
              </td>
            </ng-container>

            <!-- Client Column -->
            <ng-container matColumnDef="clientName">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase">Cliente</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 text-gray-600 text-sm">{{ row.clientName }}</td>
            </ng-container>

            <!-- Issue Date Column -->
            <ng-container matColumnDef="issueDate">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase">Emissão</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 text-gray-500 text-sm">
                {{ row.issueDate | date:'dd/MM/yyyy' || 'N/A' }}
              </td>
            </ng-container>

            <!-- Due Date Column -->
            <ng-container matColumnDef="dueDate">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase">Vencimento</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 text-gray-500 text-sm">
                {{ row.dueDate | date:'dd/MM/yyyy' || 'N/A' }}
              </td>
            </ng-container>

            <!-- Total Column -->
            <ng-container matColumnDef="total">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase">Valor</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 text-gray-900 font-semibold text-sm">
                {{ formatCurrency(row.total) }}
              </td>
            </ng-container>

            <!-- Status Column -->
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase">Status</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 text-sm">
                <span class="px-2.5 py-1 rounded-full text-xs font-bold"
                  [ngClass]="{
                    'bg-green-100 text-green-700': row.status === 'PAID',
                    'bg-blue-100 text-blue-700': row.status === 'ISSUED',
                    'bg-gray-100 text-gray-600': row.status === 'DRAFT',
                    'bg-yellow-100 text-yellow-700': row.status === 'OVERDUE',
                    'bg-red-100 text-red-700': row.status === 'CANCELLED'
                  }">
                  {{ translateStatus(row.status) }}
                </span>
              </td>
            </ng-container>

            <!-- Actions Column -->
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="py-4 px-6 text-gray-500 font-semibold text-xs uppercase text-right">Ações</th>
              <td mat-cell *matCellDef="let row" class="py-4 px-6 text-right space-x-1">
                
                <!-- Baixar PDF da Fatura -->
                <button mat-icon-button matTooltip="Baixar Fatura (PDF)" (click)="downloadPdf(row)">
                  <mat-icon class="text-red-500 scale-90">picture_as_pdf</mat-icon>
                </button>

                <!-- Editar -->
                <button mat-icon-button matTooltip="Editar" (click)="openDialog(row)">
                  <mat-icon class="text-amber-500 scale-90">edit</mat-icon>
                </button>

                <!-- Excluir -->
                <button mat-icon-button matTooltip="Excluir" (click)="onDelete(row)">
                  <mat-icon class="text-red-500 scale-90">delete</mat-icon>
                </button>

              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns" class="bg-gray-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-gray-50/30 transition-colors border-b border-gray-100"></tr>
          </table>

          @if (totalElements > 0) {
            <mat-paginator
              [length]="totalElements"
              [pageSize]="pageSize"
              [pageSizeOptions]="[5, 10, 25, 50]"
              (page)="onPage($event)"
              showFirstLastButtons
              class="border-t border-gray-100"
            ></mat-paginator>
          }
        </div>
      }
    </div>
  `
})
export class InvoicesListComponent implements OnInit {
  items: Invoice[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'issueDate,desc';
  loading = true;

  displayedColumns: string[] = ['invoiceNumber', 'clientName', 'issueDate', 'dueDate', 'total', 'status', 'actions'];

  fields: FieldDef[] = [
    { key: 'invoiceNumber', label: 'Nº da Fatura', type: 'text' },
    { key: 'clientId', label: 'Cliente', type: 'select', required: true, options: [] },
    { key: 'contractId', label: 'Contrato Relacionado', type: 'select', options: [] },
    { key: 'issueDate', label: 'Data de Emissão', type: 'date', required: true },
    { key: 'dueDate', label: 'Data de Vencimento', type: 'date', required: true },
    { key: 'paidDate', label: 'Data de Pagamento', type: 'date' },
    {
      key: 'status', label: 'Status', type: 'select',
      options: [
        { value: 'DRAFT', label: 'Rascunho' },
        { value: 'ISSUED', label: 'Emitida' },
        { value: 'PAID', label: 'Paga' },
        { value: 'OVERDUE', label: 'Vencida' },
        { value: 'CANCELLED', label: 'Cancelada' },
      ]
    },
    { key: 'subtotal', label: 'Subtotal', type: 'number' },
    { key: 'taxAmount', label: 'Impostos', type: 'number' },
    { key: 'discountAmount', label: 'Desconto', type: 'number' },
    { key: 'total', label: 'Valor Total', type: 'number' },
    { key: 'paymentMethod', label: 'Forma de Pagamento', type: 'text' },
    { key: 'paidAmount', label: 'Valor Pago', type: 'number' },
    { key: 'notes', label: 'Observações', type: 'textarea' },
  ];

  clients: any[] = [];
  contracts: any[] = [];

  constructor(
    private svc: BaseService<Invoice>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadRelations();
  }

  loadRelations(): void {
    this.loading = true;
    this.svc.getPage('clients', 0, 1000, 'name,asc').subscribe({
      next: (cPage: any) => {
        this.clients = cPage.content;
        const cField = this.fields.find(f => f.key === 'clientId');
        if (cField) cField.options = this.clients.map(c => ({ value: c.id, label: c.name }));
        this.svc.getPage('contracts', 0, 1000, 'title,asc').subscribe({
          next: (dPage: any) => {
            this.contracts = dPage.content;
            const dField = this.fields.find(f => f.key === 'contractId');
            if (dField) dField.options = [{ value: null, label: 'Nenhum' }, ...this.contracts.map(d => ({ value: d.id, label: d.title }))];
            this.load();
          },
          error: () => this.load(),
        });
      },
      error: () => this.load(),
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('invoices', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Invoice>) => {
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
    this.sort = e.active && e.direction ? `${e.active},${e.direction}` : 'issueDate,desc';
    this.load();
  }

  openDialog(entity?: any): void {
    const formEntity = entity ? {
      invoiceNumber: entity.invoiceNumber,
      clientId: entity.clientId,
      contractId: entity.contractId,
      issueDate: entity.issueDate,
      dueDate: entity.dueDate,
      paidDate: entity.paidDate,
      status: entity.status,
      subtotal: entity.subtotal,
      taxAmount: entity.taxAmount,
      discountAmount: entity.discountAmount,
      total: entity.total,
      paymentMethod: entity.paymentMethod,
      paidAmount: entity.paidAmount,
      notes: entity.notes,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Fatura' : 'Nova Fatura',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '600px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('invoices', entity.id!, result)
          : this.svc.create('invoices', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.loadRelations();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Invoice): void {
    if (!confirm('Deseja excluir esta fatura?')) return;
    this.svc.delete('invoices', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluída!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }

  downloadPdf(invoice: Invoice): void {
    const api = '/api/v1';
    const url = `${api}/invoices/${invoice.id}/pdf`;
    const token = localStorage.getItem('token');
    
    fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(res => {
      if (!res.ok) throw new Error();
      return res.blob();
    })
    .then(blob => {
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = `fatura-${invoice.invoiceNumber || invoice.id}.pdf`;
      link.click();
    })
    .catch(() => {
      this.snackBar.open('Erro ao baixar PDF da fatura', 'OK', { duration: 3000 });
    });
  }

  downloadReportPdf(): void {
    const api = '/api/v1';
    const url = `${api}/invoices/report/pdf`;
    const token = localStorage.getItem('token');
    
    fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(res => {
      if (!res.ok) throw new Error();
      return res.blob();
    })
    .then(blob => {
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = `relatorio-faturamento.pdf`;
      link.click();
    })
    .catch(() => {
      this.snackBar.open('Erro ao baixar relatório de faturamento', 'OK', { duration: 3000 });
    });
  }

  formatCurrency(value: number | null | undefined): string {
    if (value == null) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  translateStatus(status: string | null | undefined): string {
    if (!status) return 'Rascunho';
    switch (status) {
      case 'ISSUED': return 'Emitida';
      case 'PAID': return 'Paga';
      case 'OVERDUE': return 'Vencida';
      case 'CANCELLED': return 'Cancelada';
      default: return 'Rascunho';
    }
  }
}
