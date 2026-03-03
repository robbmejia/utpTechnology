import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';
import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';

import { MatPaginatorIntl } from '@angular/material/paginator';
import { routes } from './app.routes';
import { tokenExpiredInterceptor } from './interceptors/token-expired-interceptor';
import { tokenInterceptor } from './interceptors/token-interceptor';
import { getSpanishPaginatorIntl } from './intl/spanish-paginator-intl';

import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { provideAuth, getAuth } from '@angular/fire/auth';
import { provideFirestore, getFirestore } from '@angular/fire/firestore';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes, withHashLocation()),
    provideHttpClient(
      withFetch(),
      withInterceptors([tokenInterceptor, tokenExpiredInterceptor])
    ),
    provideFirebaseApp(() => initializeApp(environment.firebaseConfig)),
    provideAuth(() => getAuth()),
    provideFirestore(() => getFirestore()),
    { provide: MatPaginatorIntl, useValue: getSpanishPaginatorIntl() },
  ],
};
