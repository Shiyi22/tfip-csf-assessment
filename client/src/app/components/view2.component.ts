import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription, firstValueFrom } from 'rxjs';
import { Bundle } from '../models';

@Component({
  selector: 'app-view2',
  templateUrl: './view2.component.html',
  styleUrls: ['./view2.component.css']
})
export class View2Component implements OnInit , OnDestroy {

  param$!: Subscription
  bundleId!: string
  bundle!: Bundle
  bundleLoaded: boolean = false; 

  constructor(private actRoute: ActivatedRoute, private http: HttpClient) {}

  ngOnInit(): void {
    // get param
    this.param$ = this.actRoute.params.subscribe( (params) => {
      this.bundleId = params['bundleId']
      
      // send http request to get bundle
      firstValueFrom(this.http.get(`https://adamant-taste-production.up.railway.app/bundle/${this.bundleId}`)).then( (result: any) => {
        this.bundle = result
        this.bundleLoaded = true;
        console.info('>>> Bundle: ', this.bundle)
      })
    })
  }

  ngOnDestroy(): void {
    this.param$.unsubscribe(); 
  }

}
