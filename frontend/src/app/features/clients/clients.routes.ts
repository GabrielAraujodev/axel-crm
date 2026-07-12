import { Routes } from '@angular/router';
import { ClientsListComponent } from './clients-list.component';

export const CLIENTS_ROUTES: Routes = [
  { path: '', component: ClientsListComponent },
  {
    path: ':id',
    loadComponent: () =>
      import('./client-detail.component').then(m => m.ClientDetailComponent),
  }
];

