import { Routes } from '@angular/router';
import { ProjectsListComponent } from './projects-list.component';
import { ProjectDetailComponent } from './project-detail.component';

export const PROJECTS_ROUTES: Routes = [
  { path: '', component: ProjectsListComponent },
  { path: ':id', component: ProjectDetailComponent },
];
