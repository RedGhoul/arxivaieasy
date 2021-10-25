import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaperComponent } from '../list/paper.component';
import { PaperDetailComponent } from '../detail/paper-detail.component';
import { PaperUpdateComponent } from '../update/paper-update.component';
import { PaperRoutingResolveService } from './paper-routing-resolve.service';

const paperRoute: Routes = [
  {
    path: '',
    component: PaperComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaperDetailComponent,
    resolve: {
      paper: PaperRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaperUpdateComponent,
    resolve: {
      paper: PaperRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaperUpdateComponent,
    resolve: {
      paper: PaperRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paperRoute)],
  exports: [RouterModule],
})
export class PaperRoutingModule {}
