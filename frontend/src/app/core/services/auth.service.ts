import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/models';

const TOKEN_KEY = 'crm_token';
const USER_KEY = 'crm_user';
const TENANT_KEY = 'crm_tenant';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUser$ = new BehaviorSubject<User | null>(this.storedUser());

  constructor(private http: HttpClient, private router: Router) {}

  get user$(): Observable<User | null> {
    return this.currentUser$.asObservable();
  }

  get currentUser(): User | null {
    return this.currentUser$.value;
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  get tenantId(): string | null {
    return localStorage.getItem(TENANT_KEY);
  }

  get isAuthenticated(): boolean {
    return !!this.token;
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, req)
      .pipe(tap(res => this.handleAuth(res)));
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, req)
      .pipe(tap(res => this.handleAuth(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TENANT_KEY);
    this.currentUser$.next(null);
    this.router.navigate(['/login']);
  }

  private handleAuth(res: AuthResponse): void {
    const user: User = {
      id: res.userId,
      name: res.userName,
      email: res.email,
      role: res.role,
      organizationId: res.organizationId,
    };
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    if (res.organizationId) {
      localStorage.setItem(TENANT_KEY, res.organizationId);
    }
    this.currentUser$.next(user);
  }

  private storedUser(): User | null {
    const json = localStorage.getItem(USER_KEY);
    return json ? JSON.parse(json) : null;
  }
}
