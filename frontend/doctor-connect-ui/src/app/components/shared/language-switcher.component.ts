import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LanguageService, Language, LanguageOption } from '../../services/language.service';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './language-switcher.component.html',
  styleUrl: './language-switcher.component.scss'
})
export class LanguageSwitcherComponent {
  isOpen = false;
  currentLanguage: Language;
  languages: LanguageOption[];

  constructor(private languageService: LanguageService) {
    this.languages = this.languageService.languages;
    this.currentLanguage = this.languageService.getCurrentLanguage();

    this.languageService.currentLanguage$.subscribe(lang => {
      this.currentLanguage = lang;
    });
  }

  toggleDropdown(): void {
    this.isOpen = !this.isOpen;
  }

  selectLanguage(language: Language): void {
    this.languageService.setLanguage(language);
    this.isOpen = false;
  }

  getCurrentLanguageName(): string {
    return this.languageService.getLanguageName(this.currentLanguage);
  }
}
