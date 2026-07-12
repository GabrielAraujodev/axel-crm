import { Routes } from '@angular/router';
import { ClientPortalComponent } from './client-portal.component';
import { PartnerPortalComponent } from './partner-portal.component';

export const PORTAL_ROUTES: Routes = [
  { path: 'client', component: ClientPortalComponent },
  { path: 'partner', component: PartnerPortalComponent },
];
