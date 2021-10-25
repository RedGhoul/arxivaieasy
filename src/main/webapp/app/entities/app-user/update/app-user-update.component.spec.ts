jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AppUserService } from '../service/app-user.service';
import { IAppUser, AppUser } from '../app-user.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { AppUserUpdateComponent } from './app-user-update.component';

describe('Component Tests', () => {
  describe('AppUser Management Update Component', () => {
    let comp: AppUserUpdateComponent;
    let fixture: ComponentFixture<AppUserUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let appUserService: AppUserService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AppUserUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AppUserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppUserUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      appUserService = TestBed.inject(AppUserService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const appUser: IAppUser = { id: 456 };
        const user: IUser = { id: 80166 };
        appUser.user = user;

        const userCollection: IUser[] = [{ id: 18619 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const appUser: IAppUser = { id: 456 };
        const user: IUser = { id: 29309 };
        appUser.user = user;

        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(appUser));
        expect(comp.usersSharedCollection).toContain(user);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<AppUser>>();
        const appUser = { id: 123 };
        jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: appUser }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(appUserService.update).toHaveBeenCalledWith(appUser);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<AppUser>>();
        const appUser = new AppUser();
        jest.spyOn(appUserService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: appUser }));
        saveSubject.complete();

        // THEN
        expect(appUserService.create).toHaveBeenCalledWith(appUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<AppUser>>();
        const appUser = { id: 123 };
        jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(appUserService.update).toHaveBeenCalledWith(appUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
