import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { BaseService } from '../../core/services/base.service';
import { TimelineComponent } from '../../shared/timeline/timeline.component';

export interface ProjectDetail {
  id?: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  budget: number;
  cost: number;
  status: string;
  clientId: string;
  clientName?: string;
  managerUserId: string;
  managerName?: string;
  legalProcessId: string;
  legalProcessCnj?: string;
  cnjNumber: string;
  expertType: string;
  paymentStatus: string;
  deliveryDeadline: string;
}

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatTabsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    TimelineComponent
  ],
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  projectId = '';
  project: ProjectDetail | null = null;
  loading = true;
  saving = false;

  clients: any[] = [];
  users: any[] = [];
  processes: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private svc: BaseService<any>,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('id') || '';
    this.loadRelationsAndProject();
  }

  loadRelationsAndProject(): void {
    this.loading = true;
    this.svc.getPage('clients', 0, 1000, 'name,asc').subscribe({
      next: (cPage: any) => {
        this.clients = cPage.content;
        this.svc.getPage('users', 0, 1000, 'name,asc').subscribe({
          next: (uPage: any) => {
            this.users = uPage.content;
            this.svc.getPage('legal-processes', 0, 1000, 'cnjNumber,asc').subscribe({
              next: (lPage: any) => {
                this.processes = lPage.content;
                this.loadProject();
              },
              error: () => this.loadProject()
            });
          },
          error: () => this.loadProject()
        });
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Erro ao carregar relações', 'OK', { duration: 3000 });
      }
    });
  }

  loadProject(): void {
    this.http.get<ProjectDetail>(`${environment.apiUrl}/projects/${this.projectId}`).subscribe({
      next: (res) => {
        this.project = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Erro ao carregar projeto', 'OK', { duration: 3000 });
      }
    });
  }

  save(): void {
    if (!this.project) return;
    this.saving = true;

    // Map clean IDs
    const payload = {
      ...this.project,
      clientId: this.project.clientId || null,
      managerUserId: this.project.managerUserId || null,
      legalProcessId: this.project.legalProcessId || null
    };

    this.http.put(`${environment.apiUrl}/projects/${this.projectId}`, payload).subscribe({
      next: () => {
        this.saving = false;
        this.snackBar.open('Projeto atualizado com sucesso!', 'OK', { duration: 3000 });
        this.loadRelationsAndProject();
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Erro ao salvar projeto', 'OK', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/projects']);
  }
}
