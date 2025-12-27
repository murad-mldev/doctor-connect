# Internationalization (i18n) Guide for Doctor Connect UI

This guide explains how to use the bilingual support (English and Bangla) implemented in the Doctor Connect UI application.

## Overview

The application uses **ngx-translate** for runtime translation support, allowing users to switch between English and Bangla languages seamlessly.

## Key Components

### 1. Translation Files
Translation files are located in `src/assets/i18n/`:
- `en.json` - English translations
- `bn.json` - Bangla (Bengali) translations

### 2. Language Service
The `LanguageService` (`src/app/services/language.service.ts`) manages language state and provides methods for:
- Getting current language
- Setting/switching language
- Accessing translations programmatically

### 3. Language Switcher Component
The `LanguageSwitcherComponent` (`src/app/components/shared/language-switcher.component.ts`) provides a dropdown UI for language selection.

## Using Translations in Components

### Method 1: Using the Translate Pipe (Recommended for Templates)

```typescript
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-example',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  template: `
    <h1>{{ 'common.appName' | translate }}</h1>
    <button>{{ 'common.submit' | translate }}</button>
    <p>{{ 'dashboard.welcome' | translate }}</p>
  `
})
```

### Method 2: Using TranslateService (For Component Logic)

```typescript
import { TranslateService } from '@ngx-translate/core';

export class ExampleComponent {
  constructor(private translate: TranslateService) {}

  showMessage(): void {
    const message = this.translate.instant('common.success');
    alert(message);
  }

  // For async translations
  getAsyncMessage(): void {
    this.translate.get('common.loading').subscribe(text => {
      console.log(text);
    });
  }
}
```

### Method 3: Using LanguageService (Convenient Wrapper)

```typescript
import { LanguageService } from '../../services';

export class ExampleComponent {
  constructor(private languageService: LanguageService) {}

  showMessage(): void {
    const message = this.languageService.translateInstant('common.success');
    alert(message);
  }
}
```

## Translation Keys Structure

The translation files are organized into namespaced categories:

```json
{
  "common": {
    "appName": "Doctor Connect",
    "signIn": "Sign in",
    ...
  },
  "auth": {
    "signInTitle": "Sign in to Doctor Connect",
    ...
  },
  "dashboard": { ... },
  "doctor": { ... },
  "appointment": { ... },
  "prescription": { ... },
  "payment": { ... },
  "notification": { ... },
  "profile": { ... },
  "video": { ... },
  "navigation": { ... },
  "validation": { ... }
}
```

## Adding the Language Switcher to Your Component

```typescript
import { LanguageSwitcherComponent } from './components/shared/language-switcher.component';

@Component({
  standalone: true,
  imports: [CommonModule, LanguageSwitcherComponent],
  template: `
    <app-language-switcher></app-language-switcher>
  `
})
```

## Adding New Translation Keys

1. Open both `src/assets/i18n/en.json` and `src/assets/i18n/bn.json`
2. Add the same key to both files with appropriate translations:

**en.json:**
```json
{
  "myFeature": {
    "title": "My Feature Title",
    "description": "This is a description"
  }
}
```

**bn.json:**
```json
{
  "myFeature": {
    "title": "আমার ফিচার শিরোনাম",
    "description": "এটি একটি বিবরণ"
  }
}
```

3. Use in your component:
```html
<h1>{{ 'myFeature.title' | translate }}</h1>
<p>{{ 'myFeature.description' | translate }}</p>
```

## Translation with Parameters

You can pass dynamic values to translations:

**Translation file:**
```json
{
  "greeting": "Hello, {{name}}!"
}
```

**Usage:**
```html
{{ 'greeting' | translate:{ name: userName } }}
```

## Checking Current Language

```typescript
import { LanguageService } from '../../services';

export class ExampleComponent {
  constructor(private languageService: LanguageService) {
    // Get current language
    const currentLang = this.languageService.getCurrentLanguage(); // 'en' or 'bn'

    // Subscribe to language changes
    this.languageService.currentLanguage$.subscribe(lang => {
      console.log('Language changed to:', lang);
    });
  }
}
```

## Programmatically Changing Language

```typescript
import { LanguageService } from '../../services';

export class ExampleComponent {
  constructor(private languageService: LanguageService) {}

  switchToBangla(): void {
    this.languageService.setLanguage('bn');
  }

  switchToEnglish(): void {
    this.languageService.setLanguage('en');
  }

  toggleLanguage(): void {
    this.languageService.toggleLanguage();
  }
}
```

## Best Practices

1. **Always add keys to both language files** - Keep en.json and bn.json in sync
2. **Use descriptive key names** - Use namespaced keys like `feature.action.label`
3. **Avoid hardcoded text** - Always use translation keys instead of hardcoded strings
4. **Use the translate pipe in templates** - It's more performant than calling the service
5. **Handle missing translations gracefully** - The system will fall back to the key name if translation is missing
6. **Test both languages** - Always test your UI in both English and Bangla

## Language Persistence

The selected language is automatically saved to localStorage and persists across sessions. When a user returns to the app, their language preference is restored.

## Troubleshooting

### Translations not showing
1. Check that TranslateModule is imported in your component
2. Verify the translation key exists in both en.json and bn.json
3. Ensure the JSON files are valid (no syntax errors)

### Language not switching
1. Check browser console for errors
2. Verify the language files are being loaded correctly
3. Ensure the LanguageService is properly injected

### Missing translations appear as keys
This is expected behavior. Add the missing key to both translation files.

## Example: Complete Component with i18n

```typescript
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services';

@Component({
  selector: 'app-example',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  template: `
    <div>
      <h1>{{ 'myFeature.title' | translate }}</h1>
      <p>{{ 'myFeature.description' | translate }}</p>
      <button (click)="handleClick()">
        {{ 'common.submit' | translate }}
      </button>
      <p>{{ 'myFeature.currentLang' | translate }}: {{ currentLanguage }}</p>
    </div>
  `
})
export class ExampleComponent {
  currentLanguage: string;

  constructor(private languageService: LanguageService) {
    this.currentLanguage = this.languageService.getCurrentLanguage();

    this.languageService.currentLanguage$.subscribe(lang => {
      this.currentLanguage = lang;
    });
  }

  handleClick(): void {
    const message = this.languageService.translateInstant('common.success');
    alert(message);
  }
}
```

## Resources

- [ngx-translate Documentation](https://github.com/ngx-translate/core)
- Translation files: `src/assets/i18n/`
- Language Service: `src/app/services/language.service.ts`
- Language Switcher: `src/app/components/shared/language-switcher.component.ts`
