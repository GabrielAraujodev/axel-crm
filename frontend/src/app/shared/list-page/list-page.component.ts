import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';

export interface ColumnDef {
  key: string;
  label: string;
}

export interface KpiDef {
  label: string;
  value: string | number;
  icon: string;
  color: string;
}

@Component({
  selector: 'app-list-page',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './list-page.component.html',
  styleUrl: './list-page.component.scss',
})
export class ListPageComponent {
  @Input() title = '';
  @Input() columns: ColumnDef[] = [];
  @Input() data: any[] = [];
  @Input() totalElements = 0;
  @Input() pageSize = 10;
  @Input() loading = false;
  @Input() error: string | null = null;
  @Input() emptyMessage = 'Nenhum registro encontrado.';
  @Input() emptyIcon = 'inbox';
  @Input() emptyActionLabel = '';
  @Input() kpis: KpiDef[] = [];

  @Output() pageChange = new EventEmitter<PageEvent>();
  @Output() sortChange = new EventEmitter<Sort>();
  @Output() add = new EventEmitter<void>();
  @Output() edit = new EventEmitter<any>();
  @Output() remove = new EventEmitter<any>();
  @Output() view = new EventEmitter<any>();
  @Output() retry = new EventEmitter<void>();

  get displayedColumns(): string[] {
    return [...this.columns.map(c => c.key), 'actions'];
  }

  onPage(event: PageEvent): void {
    this.pageChange.emit(event);
  }

  onSort(event: Sort): void {
    this.sortChange.emit(event);
  }

  onAdd(): void {
    this.add.emit();
  }

  onEdit(row: any): void {
    this.edit.emit(row);
  }

  onRemove(row: any): void {
    this.remove.emit(row);
  }

  onView(row: any): void {
    this.view.emit(row);
  }

  onRetry(): void {
    this.retry.emit();
  }
}

