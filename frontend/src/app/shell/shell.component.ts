import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { HttpClient } from '@angular/common/http';
import { Subject, interval, switchMap, takeUntil, filter } from 'rxjs';
import { AuthService } from '../core/services/auth.service';
import { environment } from '../../environments/environment';
import { GlobalTimerComponent } from './timer.component';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatBadgeModule,
    GlobalTimerComponent,
  ],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent implements OnInit, OnDestroy {
  isMobile = false;
  sidenavOpened = true;
  isDarkTheme = true;
  private destroy$ = new Subject<void>();

  toggleTheme(): void {
    this.isDarkTheme = !this.isDarkTheme;
    if (this.isDarkTheme) {
      document.documentElement.removeAttribute('data-theme');
    } else {
      document.documentElement.setAttribute('data-theme', 'light');
    }
  }

  userName = '';
  notifications: any[] = [];
  unreadCount = 0;
  private pollingSub?: any;
  private pollingFailCount = 0;

  breadcrumbs: { label: string; route: string }[] = [];

  private   routeLabels: Record<string, string> = {
    'dashboard': 'Dashboard',
    'products': 'Produtos',
    'calendar': 'Agenda',
    'clients': 'Clientes',
    'partners': 'Parceiros',
    'prospects': 'Prospecção',
    'leads': 'Leads',
    'contacts': 'Contatos',
    'deals': 'Negócios',
    'pipelines': 'Pipelines',
    'projects': 'Projetos',
    'tasks': 'Tarefas',
    'proposals': 'Propostas',
    'campaigns': 'Campanhas',
    'contracts': 'Contratos',
    'tickets': 'Tickets',
    'invoices': 'Faturamento',
    'transactions': 'Transações',
    'bank-accounts': 'Contas Bancárias',
    'time-entries': 'Horas',
    'commissions': 'Comissões',
    'users': 'Usuários',
    'chart-of-accounts': 'Plano de Contas',
    'reports': 'Relatórios',
    'legal-processes': 'Processos',
    'documents': 'Documentos',
    'integrations': 'Integrações',
  };

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
  ];

  navSections = [
    {
      label: 'CRM',
      items: [
        { label: 'Prospecção', icon: 'person_search', route: '/prospects' },
        { label: 'Agenda', icon: 'calendar_month', route: '/calendar' },
        { label: 'Parceiros / Indicadores', icon: 'handshake', route: '/partners' },
        { label: 'Clientes', icon: 'people', route: '/clients' },
        { label: 'Contatos', icon: 'contacts', route: '/contacts' },
        { label: 'Leads', icon: 'leaderboard', route: '/leads' },
        { label: 'Negócios', icon: 'handshake', route: '/deals' },
        { label: 'Pipelines', icon: 'linear_scale', route: '/pipelines' },
      ]
    },
    {
      label: 'Operações',
      items: [
        { label: 'Projetos', icon: 'folder', route: '/projects' },
        { label: 'Produtos', icon: 'inventory_2', route: '/products' },
        { label: 'Contratos', icon: 'description', route: '/contracts' },
        { label: 'Processos', icon: 'gavel', route: '/legal-processes' },
        { label: 'Tarefas', icon: 'task_alt', route: '/tasks' },
        { label: 'Propostas', icon: 'description', route: '/proposals' },
        { label: 'Campanhas', icon: 'campaign', route: '/campaigns' },
        { label: 'Tickets', icon: 'support_agent', route: '/tickets' },
        { label: 'Documentos', icon: 'folder_open', route: '/documents' },
      ]
    },
    {
      label: 'Financeiro',
      items: [
        { label: 'Faturamento', icon: 'receipt', route: '/invoices' },
        { label: 'Transações', icon: 'payments', route: '/transactions' },
        { label: 'Plano de Contas', icon: 'account_tree', route: '/chart-of-accounts' },
        { label: 'Relatórios', icon: 'analytics', route: '/reports' },
        { label: 'Contas Bancárias', icon: 'account_balance', route: '/bank-accounts' },
        { label: 'Horas', icon: 'schedule', route: '/time-entries' },
        { label: 'Comissões', icon: 'monetization_on', route: '/commissions' },
      ]
    },
    {
      label: 'Admin',
      items: [
        { label: 'Usuários', icon: 'manage_accounts', route: '/users' },
        { label: 'Integrações', icon: 'settings_ethernet', route: '/integrations' },
      ]
    },
  ];

  constructor(
    private breakpointObserver: BreakpointObserver,
    private authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {
    this.breakpointObserver
      .observe([Breakpoints.Handset])
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        this.isMobile = result.matches;
        this.sidenavOpened = !this.isMobile;
      });

    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe(() => this.updateBreadcrumbs());
  }

  private updateBreadcrumbs(): void {
    const segments = this.router.url.split('/').filter(s => s);
    this.breadcrumbs = segments.map((segment, i) => ({
      label: this.routeLabels[segment] || segment,
      route: '/' + segments.slice(0, i + 1).join('/')
    }));
  }

  ngOnInit(): void {
    this.authService.user$.pipe(takeUntil(this.destroy$)).subscribe(u => {
      this.userName = u ? u.name : '';
      if (u) {
        this.startNotificationPolling();
      } else {
        this.stopNotificationPolling();
      }
    });
  }

  startNotificationPolling(): void {
    this.loadNotifications();
    this.pollingSub = interval(15000)
      .pipe(
        takeUntil(this.destroy$),
        switchMap(() => {
          const api = environment.apiUrl;
          return this.http.get<any>(`${api}/notifications?size=10&sort=createdAt,desc`);
        })
      )
      .subscribe({
        next: (res: any) => {
          this.pollingFailCount = 0;
          this.notifications = res.content || [];
          this.unreadCount = this.notifications.filter(n => !n.read).length;
        }
      });
  }

  stopNotificationPolling(): void {
    if (this.pollingSub) {
      this.pollingSub.unsubscribe();
      this.pollingSub = null;
    }
  }

  loadNotifications(): void {
    const api = environment.apiUrl;
    this.http.get<any>(`${api}/notifications?size=10&sort=createdAt,desc`).subscribe({
      next: (res: any) => {
        this.pollingFailCount = 0;
        this.notifications = res.content || [];
        this.unreadCount = this.notifications.filter(n => !n.read).length;
      },
      error: () => {
        this.pollingFailCount++;
        if (this.pollingFailCount >= 5) {
          this.stopNotificationPolling();
        }
      }
    });
  }

  markAsRead(notification: any): void {
    if (notification.read) return;
    const api = environment.apiUrl;
    this.http.put<any>(`${api}/notifications/${notification.id}/read`, {}).subscribe({
      next: () => {
        notification.read = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      }
    });
  }

  markAllAsRead(): void {
    const api = environment.apiUrl;
    this.http.put<any>(`${api}/notifications/read-all`, {}).subscribe({
      next: () => {
        this.notifications.forEach(n => n.read = true);
        this.unreadCount = 0;
      }
    });
  }

  toggleSidenav(): void {
    this.sidenavOpened = !this.sidenavOpened;
  }

  logout(): void {
    this.stopNotificationPolling();
    this.authService.logout();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.stopNotificationPolling();
  }
}
