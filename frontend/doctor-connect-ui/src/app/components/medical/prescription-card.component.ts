import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Prescription } from '../../models';

@Component({
  selector: 'app-prescription-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="border-b border-gray-200 pb-4 mb-4">
        <div class="flex justify-between items-start">
          <div>
            <h3 class="text-lg font-bold text-gray-900">Prescription</h3>
            <p class="text-sm text-gray-600 mt-1">Date: {{ prescription.prescriptionDate | date: 'medium' }}</p>
          </div>
          <span class="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full">
            ID: {{ prescription.id?.substring(0, 8) }}
          </span>
        </div>
      </div>

      <div class="space-y-4">
        <div>
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Doctor Information</h4>
          <p class="text-sm text-gray-900">Dr. {{ prescription.doctor?.user?.fullName }}</p>
          <p class="text-xs text-gray-600">{{ prescription.doctor?.specialization }}</p>
        </div>

        <div>
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Patient Information</h4>
          <p class="text-sm text-gray-900">{{ prescription.patient?.user?.fullName }}</p>
        </div>

        <div *ngIf="prescription.diagnosis">
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Diagnosis</h4>
          <p class="text-sm text-gray-600">{{ prescription.diagnosis }}</p>
        </div>

        <div *ngIf="prescription.medicines && prescription.medicines.length > 0">
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Prescribed Medicines</h4>
          <div class="space-y-2">
            <div *ngFor="let medicine of prescription.medicines" class="bg-gray-50 p-3 rounded-lg">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ medicine.name }}</p>
                  <p class="text-xs text-gray-600 mt-1">{{ medicine.dosage }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ medicine.genericName }}</p>
                </div>
                <span class="text-xs text-blue-600 font-medium">{{ medicine.type }}</span>
              </div>
            </div>
          </div>
        </div>

        <div *ngIf="prescription.labTests && prescription.labTests.length > 0">
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Recommended Lab Tests</h4>
          <div class="space-y-2">
            <div *ngFor="let test of prescription.labTests" class="bg-blue-50 p-3 rounded-lg">
              <p class="text-sm font-medium text-blue-900">{{ test.name }}</p>
              <p class="text-xs text-blue-700 mt-1">{{ test.description }}</p>
            </div>
          </div>
        </div>

        <div *ngIf="prescription.notes">
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Additional Notes</h4>
          <p class="text-sm text-gray-600">{{ prescription.notes }}</p>
        </div>

        <div *ngIf="prescription.followUpDate">
          <div class="bg-yellow-50 p-3 rounded-lg flex items-start">
            <svg class="h-5 w-5 text-yellow-600 mr-2 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <div>
              <p class="text-sm font-medium text-yellow-900">Follow-up Required</p>
              <p class="text-xs text-yellow-700 mt-1">{{ prescription.followUpDate | date: 'medium' }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-6 pt-4 border-t border-gray-200 flex space-x-2">
        <button class="flex-1 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700">
          Download PDF
        </button>
        <button class="px-4 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-200">
          Print
        </button>
      </div>
    </div>
  `,
  styles: []
})
export class PrescriptionCardComponent {
  @Input() prescription!: Prescription;
}
