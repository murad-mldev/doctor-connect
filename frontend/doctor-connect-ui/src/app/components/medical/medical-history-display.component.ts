import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MedicalHistory } from '../../models';

@Component({
  selector: 'app-medical-history-display',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-lg shadow-md p-6">
      <h3 class="text-xl font-bold text-gray-900 mb-6">Medical History</h3>

      <div *ngIf="!medicalHistory" class="text-center py-8 text-gray-500">
        <p>No medical history available</p>
      </div>

      <div *ngIf="medicalHistory" class="space-y-6">
        <div
          *ngIf="
            medicalHistory.allergies && medicalHistory.allergies.length > 0
          "
        >
          <h4
            class="text-sm font-semibold text-gray-700 mb-3 flex items-center"
          >
            <svg
              class="h-5 w-5 text-red-500 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
            Allergies
          </h4>
          <div class="flex flex-wrap gap-2">
            <span
              *ngFor="let allergy of medicalHistory.allergies"
              class="px-3 py-1 bg-red-100 text-red-800 text-sm rounded-full"
            >
              {{ allergy }}
            </span>
          </div>
        </div>

        <div
          *ngIf="
            medicalHistory.chronicDiseases &&
            medicalHistory.chronicDiseases.length > 0
          "
        >
          <h4
            class="text-sm font-semibold text-gray-700 mb-3 flex items-center"
          >
            <svg
              class="h-5 w-5 text-orange-500 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
              />
            </svg>
            Chronic Diseases
          </h4>
          <div class="flex flex-wrap gap-2">
            <span
              *ngFor="let disease of medicalHistory.chronicDiseases"
              class="px-3 py-1 bg-orange-100 text-orange-800 text-sm rounded-full"
            >
              {{ disease }}
            </span>
          </div>
        </div>

        <div
          *ngIf="
            medicalHistory.currentMedications &&
            medicalHistory.currentMedications.length > 0
          "
        >
          <h4
            class="text-sm font-semibold text-gray-700 mb-3 flex items-center"
          >
            <svg
              class="h-5 w-5 text-blue-500 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"
              />
            </svg>
            Current Medications
          </h4>
          <div class="space-y-2">
            <div
              *ngFor="let medication of medicalHistory.currentMedications"
              class="bg-blue-50 p-3 rounded-lg"
            >
              <p class="text-sm font-medium text-blue-900">{{ medication }}</p>
            </div>
          </div>
        </div>

        <div
          *ngIf="
            medicalHistory.pastSurgeries &&
            medicalHistory.pastSurgeries.length > 0
          "
        >
          <h4
            class="text-sm font-semibold text-gray-700 mb-3 flex items-center"
          >
            <svg
              class="h-5 w-5 text-purple-500 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M8 7v8a2 2 0 002 2h6M8 7V5a2 2 0 012-2h4.586a1 1 0 01.707.293l4.414 4.414a1 1 0 01.293.707V15a2 2 0 01-2 2h-2M8 7H6a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2v-2"
              />
            </svg>
            Past Surgeries
          </h4>
          <div class="space-y-2">
            <div
              *ngFor="let surgery of medicalHistory.pastSurgeries"
              class="bg-purple-50 p-3 rounded-lg"
            >
              <p class="text-sm font-medium text-purple-900">{{ surgery }}</p>
            </div>
          </div>
        </div>

        <div *ngIf="medicalHistory.familyHistory">
          <h4
            class="text-sm font-semibold text-gray-700 mb-3 flex items-center"
          >
            <svg
              class="h-5 w-5 text-green-500 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
              />
            </svg>
            Family History
          </h4>
          <div class="bg-green-50 p-4 rounded-lg">
            <p class="text-sm text-green-900">
              {{ medicalHistory.familyHistory }}
            </p>
          </div>
        </div>

        <div *ngIf="medicalHistory.bloodGroup">
          <h4 class="text-sm font-semibold text-gray-700 mb-3">Blood Group</h4>
          <span
            class="px-4 py-2 bg-red-100 text-red-800 text-lg font-bold rounded-lg"
          >
            {{ medicalHistory.bloodGroup }}
          </span>
        </div>

        <div *ngIf="medicalHistory.notes">
          <h4 class="text-sm font-semibold text-gray-700 mb-3">
            Additional Notes
          </h4>
          <div class="bg-gray-50 p-4 rounded-lg">
            <p class="text-sm text-gray-700">{{ medicalHistory.notes }}</p>
          </div>
        </div>

        <div
          *ngIf="medicalHistory.lastUpdated"
          class="text-xs text-gray-500 pt-4 border-t border-gray-200"
        >
          Last updated: {{ medicalHistory.lastUpdated | date : 'medium' }}
        </div>
      </div>
    </div>
  `,
  styles: [],
})
export class MedicalHistoryDisplayComponent {
  @Input() medicalHistory: MedicalHistory | null = null;
}
