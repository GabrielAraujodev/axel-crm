// Routes configuration for Leads module
import { Routes } from '@angular/router';
import { LeadsListComponent } from './leads-list.component';
import { LeadDetailComponent } from './lead-detail.component';
export const LEADS_ROUTES: Routes = [
  { path: '', component: LeadsListComponent },
  { path: ':id', component: LeadDetailComponent }
];
