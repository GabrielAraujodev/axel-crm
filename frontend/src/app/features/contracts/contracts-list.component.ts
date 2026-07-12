import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ListPageComponent, ColumnDef, KpiDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Contract, Page } from '../../core/models/models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-contracts-list',
  standalone: true,
  imports: [
    CommonModule,
    ListPageComponent,
    MatButtonModule,
    MatIconModule,
    DragDropModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
    MatButtonToggleModule,
    FormsModule
  ],
  template: `
    <div class="flex flex-col h-full">
      <div class="flex items-center justify-between p-4 border-b" style="border-color:var(--hairline);background:var(--card-bg);">
        <h1 style="margin:0;font-family:'Outfit',sans-serif;font-size:24px;font-weight:700;color:var(--ink);">Contratos</h1>
        <div class="flex items-center gap-4">
          <mat-button-toggle-group [(ngModel)]="viewMode" aria-label="Modo de visualização">
            <mat-button-toggle value="kanban"><mat-icon>view_kanban</mat-icon></mat-button-toggle>
            <mat-button-toggle value="list"><mat-icon>view_list</mat-icon></mat-button-toggle>
          </mat-button-toggle-group>
          <button mat-flat-button color="primary" (click)="openDialog()">
            <mat-icon>add</mat-icon> Novo Contrato
          </button>
        </div>
      </div>

      <!-- KANBAN VIEW -->
      <div *ngIf="viewMode === 'kanban'" class="flex-1 overflow-x-auto p-6" style="background:var(--bg);">
        <div cdkDropListGroup class="kanban-board" style="min-width:max-content;display:flex;gap:20px;align-items:start;height:100%;">
          @for (stage of stages; track stage.value) {
            <div class="kanban-col">
              <div class="kanban-col-header">
                <h3>{{ stage.label }}</h3>
                <span class="count-badge">{{ (grouped[stage.value] || []).length }}</span>
              </div>
              <div cdkDropList [cdkDropListData]="grouped[stage.value]" (cdkDropListDropped)="drop($event, stage.value)" class="kanban-list">
                @for (item of grouped[stage.value]; track item.id) {
                  <mat-card cdkDrag class="kanban-card">
                    <mat-card-content style="padding:16px;">
                      <div class="flex justify-between items-start" style="margin-bottom:12px;">
                        <div>
                          <div style="font-size:11px;color:var(--muted);margin-bottom:2px;">{{ item.contractNumber || 'N/A' }}</div>
                          <h4 style="font-family:'Outfit',sans-serif;font-weight:700;font-size:14px;margin:0;cursor:pointer;color:var(--ink);" (click)="openDialog(item)">{{ item.title }}</h4>
                        </div>
                        <button mat-icon-button (click)="openDialog(item)" style="flex-shrink:0;width:32px;height:32px;">
                          <mat-icon style="font-size:16px;width:16px;height:16px;">edit</mat-icon>
                        </button>
                      </div>
                      <div style="margin-bottom:12px;">
                        <span style="font-size:12px;color:var(--muted);">{{ item.clientName }}</span>
                      </div>
                      @if (item.value) {
                        <div class="meta-chip">
                          <mat-icon>attach_money</mat-icon>
                          {{ item.value | currency:'BRL':'symbol':'1.2-2' }}
                        </div>
                      }
                      <div class="flex items-center justify-between" style="padding-top:12px;border-top:1px solid var(--hairline);margin-top:12px;">
                        <span style="font-size:11px;color:var(--muted);">
                          @if (item.startDate) { {{ item.startDate | date:'dd/MM/yyyy' }} } @if (item.endDate) { → {{ item.endDate | date:'dd/MM/yyyy' }} }
                        </span>
                        <mat-chip style="font-size:11px;" [color]="item.status === 'ACTIVE' ? 'accent' : 'primary'" selected>
                          {{ statusLabel(item.status) }}
                        </mat-chip>
                      </div>
                    </mat-card-content>
                  </mat-card>
                }
              </div>
            </div>
          }
        </div>
      </div>

      <!-- LIST VIEW -->
      <div *ngIf="viewMode === 'list'" class="flex-1 overflow-hidden" style="padding:24px;">
        <app-list-page
          title="Contratos"
          [columns]="columns"
          [data]="items"
          [totalElements]="totalElements"
          [pageSize]="pageSize"
          [loading]="loading"
          [kpis]="kpis"
          emptyMessage="Nenhum contrato encontrado."
          emptyIcon="description"
          emptyActionLabel="Criar Contrato"
          (pageChange)="onPage($event)"
          (sortChange)="onSort($event)"
          (add)="openDialog()"
          (edit)="openDialog($event)"
          (remove)="onDelete($event)"
        ></app-list-page>
      </div>
    </div>
  `,
  styles: [`
    .kanban-col { width:300px; display:flex; flex-direction:column; max-height:100%; border-radius:16px; border:1px solid var(--hairline); background:var(--card-bg); box-shadow:0 4px 20px rgba(0,0,0,0.1); }
    .kanban-col-header { padding:16px 20px; border-bottom:1px solid var(--hairline); background:var(--bg-elevated); border-top-left-radius:16px; border-top-right-radius:16px; display:flex; justify-content:space-between; align-items:center; }
    .kanban-col-header h3 { font-family:'Outfit',sans-serif; font-weight:600; color:var(--ink); font-size:14px; margin:0; }
    .count-badge { background:var(--bg-elevated); color:var(--muted); font-size:11px; font-weight:600; padding:4px 10px; border-radius:9999px; border:1px solid var(--hairline); }
    .kanban-list { flex:1; padding:12px; overflow-y:auto; min-height:200px; display:flex; flex-direction:column; gap:10px; }
    .kanban-list::-webkit-scrollbar { width:6px; }
    .kanban-list::-webkit-scrollbar-thumb { background:var(--muted); border-radius:3px; }
    .kanban-card { border-radius:12px; border:1px solid var(--hairline); background:var(--card-bg); box-shadow:0 2px 8px rgba(0,0,0,0.1); transition:transform 0.2s,box-shadow 0.2s; }
    .kanban-card:hover { transform:translateY(-2px); box-shadow:0 8px 16px rgba(0,0,0,0.15); }
    .kanban-card.cdk-drag-placeholder { opacity:0.3; border:2px dashed var(--muted); background:transparent; }
    .meta-chip { display:inline-flex; align-items:center; gap:4px; padding:3px 8px; border-radius:6px; font-size:11px; font-weight:500; color:var(--muted); background:var(--bg-elevated); }
    .meta-chip mat-icon { font-size:14px; width:14px; height:14px; }
  `]
})
export class ContractsListComponent implements OnInit {
  items: Contract[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  viewMode: 'list' | 'kanban' = 'list';

  stages = [
    { value: 'DRAFT', label: 'Rascunho' },
    { value: 'ACTIVE', label: 'Ativo' },
    { value: 'EXPIRED', label: 'Expirado' },
    { value: 'TERMINATED', label: 'Encerrado' },
    { value: 'RENEWED', label: 'Renovado' },
  ];

  grouped: Record<string, Contract[]> = {
    'DRAFT': [], 'ACTIVE': [], 'EXPIRED': [], 'TERMINATED': [], 'RENEWED': [],
  };

  get kpis(): KpiDef[] {
    const active = this.items.filter(c => c.status === 'ACTIVE').length;
    const totalValue = this.items.reduce((s, c) => s + (Number(c.value) || 0), 0);
    return [
      { label: 'Total de Contratos', value: this.totalElements, icon: 'description', color: 'var(--primary)' },
      { label: 'Ativos', value: active, icon: 'check_circle', color: '#22c55e' },
      { label: 'Valor Total', value: totalValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }), icon: 'attach_money', color: '#f59e0b' },
    ];
  }

  columns: ColumnDef[] = [
    { key: 'contractNumber', label: 'Nº Contrato' },
    { key: 'title', label: 'Título' },
    { key: 'clientName', label: 'Cliente' },
    { key: 'status', label: 'Status' },
    { key: 'value', label: 'Valor' },
    { key: 'startDate', label: 'Início' },
    { key: 'endDate', label: 'Término' },
  ];

  fields: FieldDef[] = [
    { key: 'title', label: 'Título', type: 'text', required: true },
    { key: 'contractNumber', label: 'Nº do Contrato', type: 'text' },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'clientId', label: 'Cliente', type: 'select', required: true, options: [] },
    { key: 'dealId', label: 'Negócio Relacionado', type: 'select', options: [] },
    { key: 'startDate', label: 'Data Início', type: 'date', required: true },
    { key: 'endDate', label: 'Data Término', type: 'date' },
    { key: 'value', label: 'Valor Total', type: 'number' },
    { key: 'monthlyValue', label: 'Valor Mensal', type: 'number' },
    {
      key: 'status', label: 'Status', type: 'select',
      options: [
        { value: 'DRAFT', label: 'Rascunho' },
        { value: 'ACTIVE', label: 'Ativo' },
        { value: 'EXPIRED', label: 'Expirado' },
        { value: 'TERMINATED', label: 'Encerrado' },
        { value: 'RENEWED', label: 'Renovado' },
      ]
    },
    { key: 'terms', label: 'Termos e Condições', type: 'textarea' },
    { key: 'notes', label: 'Observações', type: 'textarea' },
    { key: 'signedByClient', label: 'Assinado por', type: 'text' },
    { key: 'autoRenew', label: 'Renovação Automática', type: 'checkbox' },
  ];

  clients: any[] = [];
  deals: any[] = [];

  constructor(
    private svc: BaseService<Contract>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private http: HttpClient,
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
        this.svc.getPage('deals', 0, 1000, 'title,asc').subscribe({
          next: (dPage: any) => {
            this.deals = dPage.content;
            const dField = this.fields.find(f => f.key === 'dealId');
            if (dField) dField.options = [{ value: null, label: 'Nenhum' }, ...this.deals.map(d => ({ value: d.id, label: d.title }))];
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
    this.svc.getPage('contracts', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Contract>) => {
        this.items = p.content;
        this.totalElements = p.totalElements;
        this.groupItems();
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });
  }

  groupItems(): void {
    this.grouped = { 'DRAFT': [], 'ACTIVE': [], 'EXPIRED': [], 'TERMINATED': [], 'RENEWED': [] };
    for (const item of this.items) {
      const s = item.status || 'DRAFT';
      if (this.grouped[s]) this.grouped[s].push(item);
      else this.grouped['DRAFT'].push(item);
    }
  }

  drop(event: any, newStatus: string): void {
    if (event.previousContainer === event.container) {
      import('@angular/cdk/drag-drop').then(m => m.moveItemInArray(event.container.data, event.previousIndex, event.currentIndex));
    } else {
      import('@angular/cdk/drag-drop').then(m => {
        m.transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
        const moved = event.container.data[event.currentIndex];
        moved.status = newStatus;
        this.svc.update('contracts', moved.id!, { status: newStatus }).subscribe({
          error: () => { this.snackBar.open('Erro ao mudar status', 'OK', { duration: 3000 }); this.load(); }
        });
      });
    }
  }

  statusLabel(status: string | undefined): string {
    const labels: Record<string, string> = { 'DRAFT': 'Rascunho', 'ACTIVE': 'Ativo', 'EXPIRED': 'Expirado', 'TERMINATED': 'Encerrado', 'RENEWED': 'Renovado' };
    return labels[status || ''] || status || 'Desconhecido';
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
      title: entity.title,
      contractNumber: entity.contractNumber,
      description: entity.description,
      clientId: entity.clientId,
      dealId: entity.dealId,
      startDate: entity.startDate,
      endDate: entity.endDate,
      value: entity.value,
      monthlyValue: entity.monthlyValue,
      status: entity.status,
      terms: entity.terms,
      notes: entity.notes,
      signedByClient: entity.signedByClient,
      autoRenew: entity.autoRenew,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Contrato' : 'Novo Contrato',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '600px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('contracts', entity.id!, result)
          : this.svc.create('contracts', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.loadRelations();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Contract): void {
    if (!confirm('Deseja excluir este contrato?')) return;
    this.svc.delete('contracts', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
