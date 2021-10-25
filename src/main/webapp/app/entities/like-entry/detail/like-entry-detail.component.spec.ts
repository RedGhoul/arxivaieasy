import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LikeEntryDetailComponent } from './like-entry-detail.component';

describe('Component Tests', () => {
  describe('LikeEntry Management Detail Component', () => {
    let comp: LikeEntryDetailComponent;
    let fixture: ComponentFixture<LikeEntryDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [LikeEntryDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ likeEntry: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(LikeEntryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LikeEntryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load likeEntry on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.likeEntry).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
