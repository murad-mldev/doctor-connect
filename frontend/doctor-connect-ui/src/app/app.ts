import { Component, signal } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Home } from './home/home';
import { filter } from 'rxjs/operators';
import { LanguageSwitcherComponent } from '../app/components/shared/language-switcher.component';
import { NavbarComponent } from './components/layout/navbar.component';
import { FooterComponent } from './components/layout/footer.component';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    Home,
    CommonModule,
    LanguageSwitcherComponent,
    NavbarComponent,
    FooterComponent,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('doctor-connect-ui');
  isRootRoute = true;
  showPublicLayout = true;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.isRootRoute = event.url === '/' || event.url === '';

        // Hide navbar and footer for dashboard and video consultation routes
        this.showPublicLayout = !event.url.includes('/dashboard') && !event.url.includes('/video-consultation');
      });
  }
}
