import { Component, OnInit } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import localePt from '@angular/common/locales/pt';

registerLocaleData(localePt);

export interface ReportItem {
  accountCode: string;
  accountName: string;
  total: number;
}

export interface ReportData {
  revenues: ReportItem[];
  expenses: ReportItem[];
  totalRevenues: number;
  totalExpenses: number;
  netResult: number;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {
  startDate = '';
  endDate = '';
  reportType: 'DRE' | 'DFC' = 'DRE';
  loading = false;
  data: ReportData | null = null;

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth(); // 0-12
    
    // Default to first day of current month
    const start = new Date(year, month, 1);
    // Default to last day of current month
    const end = new Date(year, month + 1, 0);

    this.startDate = this.formatDate(start);
    this.endDate = this.formatDate(end);

    this.generate();
  }

  setReportType(type: 'DRE' | 'DFC'): void {
    this.reportType = type;
    this.generate();
  }

  generate(): void {
    if (!this.startDate || !this.endDate) {
      this.snackBar.open('Selecione as datas de início e fim', 'OK', { duration: 3000 });
      return;
    }

    this.loading = true;
    const params = new HttpParams()
      .set('startDate', this.startDate)
      .set('endDate', this.endDate);

    const endpoint = this.reportType === 'DRE' ? 'dre' : 'dfc';

    this.http.get<ReportData>(`${environment.apiUrl}/reports/${endpoint}`, { params }).subscribe({
      next: (res) => {
        this.data = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Erro ao gerar relatório contábil', 'OK', { duration: 3000 });
      }
    });
  }

  private formatDate(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }
}
