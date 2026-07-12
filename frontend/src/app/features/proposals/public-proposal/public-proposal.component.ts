import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-public-proposal',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './public-proposal.component.html',
  styleUrl: './public-proposal.component.scss'
})
export class PublicProposalComponent implements OnInit {
  loading = true;
  error = false;
  token = '';
  proposal: any = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.token = params['token'];
      if (this.token) {
        this.loadProposal();
      } else {
        this.loading = false;
        this.error = true;
      }
    });
  }

  loadProposal(): void {
    const api = environment.apiUrl;
    this.http.get<any>(`${api}/proposals/public/${this.token}`).subscribe({
      next: (res: any) => {
        this.proposal = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = true;
      }
    });
  }

  downloadPdf(): void {
    const api = environment.apiUrl;
    const url = `${api}/proposals/public/${this.token}/pdf`;
    
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (blob: Blob) => {
        const downloadUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `proposta-${this.proposal.proposalCode || this.proposal.id}.pdf`;
        link.click();
        window.URL.revokeObjectURL(downloadUrl);
      },
      error: () => {
        alert('Erro ao fazer o download do PDF.');
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  translateStatus(status: string): string {
    const translations: Record<string, string> = {
      'DRAFT': 'Rascunho',
      'SENT': 'Enviada',
      'VIEWED': 'Visualizada',
      'NEGOTIATING': 'Em Negociação',
      'ACCEPTED': 'Aceita',
      'REJECTED': 'Rejeitada',
      'EXPIRED': 'Expirada'
    };
    return translations[status] || status;
  }
}
