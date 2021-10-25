import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SubjectDetailComponent } from './subject-detail.component';

describe('Component Tests', () => {
  describe('Subject Management Detail Component', () => {
    let comp: SubjectDetailComponent;
    let fixture: ComponentFixture<SubjectDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SubjectDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ subject: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SubjectDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SubjectDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load subject on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.subject).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
