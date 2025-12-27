import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, X } from 'lucide-angular';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="modal-overlay" *ngIf="isOpen" (click)="onOverlayClick()">
      <div
        class="modal-content"
        [class]="size"
        (click)="$event.stopPropagation()"
      >
        <div class="modal-header">
          <h2>{{ title }}</h2>
          <button class="btn-close" (click)="close()" type="button">
            <lucide-icon [img]="X" [size]="24"></lucide-icon>
          </button>
        </div>
        <div class="modal-body">
          <ng-content></ng-content>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1000;
        padding: 1rem;
      }

      .modal-content {
        background: white;
        border-radius: 8px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        max-height: 90vh;
        overflow-y: auto;
        width: 100%;

        &.small {
          max-width: 400px;
        }

        &.medium {
          max-width: 600px;
        }

        &.large {
          max-width: 800px;
        }

        &.full {
          max-width: 95%;
        }
      }

      .modal-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 1.5rem;
        border-bottom: 1px solid #e5e7eb;
      }

      .modal-header h2 {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 600;
        color: #111827;
      }

      .btn-close {
        background: none;
        border: none;
        cursor: pointer;
        color: #6b7280;
        padding: 0.25rem;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        transition: all 0.2s;
      }

      .btn-close:hover {
        background-color: #f3f4f6;
        color: #111827;
      }

      .modal-body {
        padding: 1.5rem;
      }
    `,
  ],
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = '';
  @Input() size: 'small' | 'medium' | 'large' | 'full' = 'medium';
  @Input() closeOnOverlayClick = true;
  @Output() closeModal = new EventEmitter<void>();
  @Output() isOpenChange = new EventEmitter<boolean>();

  readonly X = X;

  close(): void {
    this.isOpen = false;
    this.isOpenChange.emit(false);
    this.closeModal.emit();
  }

  onOverlayClick(): void {
    if (this.closeOnOverlayClick) {
      this.close();
    }
  }
}
