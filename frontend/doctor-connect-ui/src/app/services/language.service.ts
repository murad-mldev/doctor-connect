import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type Language = 'en' | 'bn';

export interface LanguageOption {
  code: Language;
  name: string;
  nativeName: string;
}

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private currentLanguageSubject: BehaviorSubject<Language>;
  public currentLanguage$: Observable<Language>;

  public readonly languages: LanguageOption[] = [
    { code: 'en', name: 'English', nativeName: 'English' },
    { code: 'bn', name: 'Bengali', nativeName: 'বাংলা' },
  ];

  constructor(private translate: TranslateService) {
    const savedLanguage = this.getSavedLanguage();
    this.currentLanguageSubject = new BehaviorSubject<Language>(savedLanguage);
    this.currentLanguage$ = this.currentLanguageSubject.asObservable();

    this.initializeLanguage(savedLanguage);
  }

  private initializeLanguage(language: Language): void {
    this.translate.setDefaultLang('en');
    // attempt to load preferred language, fallback to English on error
    this.translate.use(language).subscribe({
      next: () => {
        // loaded successfully
      },
      error: (err) => {
        console.warn(
          `Failed to load translations for ${language}, falling back to 'en'`,
          err
        );
        this.translate.use('en').subscribe();
      },
    });
  }

  private getSavedLanguage(): Language {
    const savedLang = localStorage.getItem('preferredLanguage') as Language;
    return savedLang && this.isValidLanguage(savedLang) ? savedLang : 'en';
  }

  private isValidLanguage(lang: string): lang is Language {
    return lang === 'en' || lang === 'bn';
  }

  public getCurrentLanguage(): Language {
    return this.currentLanguageSubject.value;
  }

  public setLanguage(language: Language): void {
    if (this.isValidLanguage(language)) {
      // attempt to switch language and update state only if successful
      this.translate.use(language).subscribe({
        next: () => {
          localStorage.setItem('preferredLanguage', language);
          this.currentLanguageSubject.next(language);
        },
        error: (err) => {
          console.warn(
            `Failed to load translations for ${language}, keeping previous language`,
            err
          );
        },
      });
    }
  }

  public toggleLanguage(): void {
    const currentLang = this.getCurrentLanguage();
    const newLang: Language = currentLang === 'en' ? 'bn' : 'en';
    this.setLanguage(newLang);
  }

  public getLanguageName(code: Language): string {
    const lang = this.languages.find((l) => l.code === code);
    return lang ? lang.nativeName : code;
  }

  public translate$(key: string, params?: any): Observable<string> {
    return this.translate.get(key, params);
  }

  public translateInstant(key: string, params?: any): string {
    return this.translate.instant(key, params);
  }
}
