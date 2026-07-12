import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  template: `
    <div class="profile-container">
      <mat-card class="profile-card">
        <div class="profile-header">
          <div class="avatar-circle">
            {{ getInitials() }}
          </div>
          <div class="profile-title-area">
            <h2>{{ userFullName || 'Meu Perfil' }}</h2>
            <span class="role-badge">{{ userRole }}</span>
          </div>
        </div>

        <mat-card-content class="profile-content">
          <form #profileForm="ngForm" (ngSubmit)="saveProfile()">
            <div class="form-grid">
              <mat-form-field appearance="outline">
                <mat-label>Nome Completo</mat-label>
                <input matInput [(ngModel)]="userFullName" name="fullName" required placeholder="Seu nome">
                <mat-icon matSuffix>person</mat-icon>
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>E-mail</mat-label>
                <input matInput type="email" [(ngModel)]="userEmail" name="email" required placeholder="exemplo@empresa.com">
                <mat-icon matSuffix>email</mat-icon>
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Organização / Empresa</mat-label>
                <input matInput [value]="organizationName" disabled placeholder="Empresa">
                <mat-icon matSuffix>business</mat-icon>
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Nova Senha (Opcional)</mat-label>
                <input matInput [type]="hidePassword ? 'password' : 'text'" [(ngModel)]="newPassword" name="password" placeholder="Digite para alterar a senha">
                <button type="button" mat-icon-button matSuffix (click)="hidePassword = !hidePassword">
                  <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
                </button>
              </mat-form-field>
            </div>

            <div class="action-row">
              <button mat-flat-button color="primary" type="submit" [disabled]="submitting || !profileForm.valid">
                <mat-icon>save</mat-icon>
                {{ submitting ? 'Salvando...' : 'Salvar Alterações' }}
              </button>
            </div>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .profile-container {
      max-width: 800px;
      margin: 24px auto;
      padding: 0 16px;
    }
    .profile-card {
      padding: 32px;
      border-radius: 12px;
      background: var(--bg-card, rgba(255, 255, 255, 0.02));
      border: 1px solid var(--hairline, rgba(255, 255, 255, 0.1));
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    }
    .profile-header {
      display: flex;
      align-items: center;
      gap: 24px;
      margin-bottom: 32px;
      padding-bottom: 24px;
      border-bottom: 1px solid var(--hairline, rgba(255, 255, 255, 0.1));
    }
    .avatar-circle {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background: linear-gradient(135deg, var(--primary, #fc6e20) 0%, #d85c15 100%);
      color: white;
      font-size: 28px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 0 15px rgba(252, 110, 32, 0.3);
      font-family: 'Outfit', sans-serif;
    }
    .profile-title-area {
      h2 {
        font-family: 'Outfit', sans-serif;
        font-size: 24px;
        font-weight: 700;
        margin: 0 0 6px 0;
        color: var(--ink, #f8fafc);
      }
      .role-badge {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 20px;
        font-size: 11px;
        font-weight: 600;
        text-transform: uppercase;
        background-color: rgba(252, 110, 32, 0.15);
        color: var(--primary, #fc6e20);
      }
    }
    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 24px;
    }
    @media (max-width: 600px) {
      .form-grid {
        grid-template-columns: 1fr;
      }
      .profile-header {
        flex-direction: column;
        text-align: center;
      }
    }
    .action-row {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
    mat-form-field {
      width: 100%;
    }
  `]
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private http = inject(HttpClient);
  private snackBar = inject(MatSnackBar);

  userId = '';
  userFullName = '';
  userEmail = '';
  userRole = 'USER';
  organizationName = '';
  newPassword = '';
  hidePassword = true;
  submitting = false;

  ngOnInit(): void {
    const user = this.authService.currentUser;
    if (user) {
      this.userId = user.id || '';
      this.userFullName = user.name || '';
      this.userEmail = user.email || '';
      this.userRole = user.role || 'USER';
      this.loadUserProfile();
    }
  }

  loadUserProfile(): void {
    const api = environment.apiUrl;
    this.http.get<any>(`${api}/users/me`).subscribe({
      next: (res) => {
        this.userFullName = res.fullName;
        this.userEmail = res.email;
        this.userRole = res.role;
        this.organizationName = res.organizationName;
      },
      error: () => {
        this.snackBar.open('Erro ao carregar os detalhes do perfil.', 'Fechar', { duration: 4000 });
      }
    });
  }

  saveProfile(): void {
    if (!this.userFullName.trim() || !this.userEmail.trim()) return;

    this.submitting = true;
    const api = environment.apiUrl;
    
    const requestPayload: any = {
      fullName: this.userFullName,
      email: this.userEmail,
      role: this.userRole,
      active: true
    };

    if (this.newPassword.trim()) {
      requestPayload.password = this.newPassword;
    }

    this.http.put<any>(`${api}/users/${this.userId}`, requestPayload).subscribe({
      next: (res) => {
        this.submitting = false;
        this.newPassword = '';
        this.authService.updateCurrentUser({
          name: res.fullName,
          email: res.email
        });
        this.snackBar.open('Perfil atualizado com sucesso!', 'OK', { duration: 3000 });
      },
      error: () => {
        this.submitting = false;
        this.snackBar.open('Erro ao atualizar o perfil. Verifique as informações.', 'Fechar', { duration: 4000 });
      }
    });
  }

  getInitials(): string {
    if (!this.userFullName) return 'U';
    const parts = this.userFullName.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return parts[0][0].toUpperCase();
  }
}
