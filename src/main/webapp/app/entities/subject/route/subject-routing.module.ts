import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SubjectComponent } from '../list/subject.component';
import { SubjectDetailComponent } from '../detail/subject-detail.component';
import { SubjectUpdateComponent } from '../update/subject-update.component';
import { SubjectRoutingResolveService } from './subject-routing-resolve.service';

const subjectRoute: Routes = [
  {
    path: '',
    component: SubjectComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SubjectDetailComponent,
    resolve: {
      subject: SubjectRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SubjectUpdateComponent,
    resolve: {
      subject: SubjectRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SubjectUpdateComponent,
    resolve: {
      subject: SubjectRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(subjectRoute)],
  exports: [RouterModule],
})
export class SubjectRoutingModule {}
