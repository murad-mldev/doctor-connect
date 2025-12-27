import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Shield, Plus, Edit2, Trash2, X, Check } from 'lucide-angular';
import { RoleService } from '../../services/role.service';
import { Role, CreateRoleRequest, UpdateRoleRequest } from '../../models';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule],
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss'
})
export class RolesComponent implements OnInit {
  readonly Shield = Shield;
  readonly Plus = Plus;
  readonly Edit2 = Edit2;
  readonly Trash2 = Trash2;
  readonly X = X;
  readonly Check = Check;

  roles: Role[] = [];
  isLoading = false;
  error: string | null = null;

  // Modal state
  showAddModal = false;
  showEditModal = false;
  showDeleteModal = false;

  // Form data
  newRoleName = '';
  editingRole: Role | null = null;
  editRoleName = '';
  deletingRole: Role | null = null;

  constructor(private roleService: RoleService) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.isLoading = true;
    this.error = null;

    this.roleService.getAllRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load roles';
        console.error('Error loading roles:', error);
        this.isLoading = false;
      }
    });
  }

  addRole(): void {
    this.showAddModal = true;
    this.newRoleName = '';
  }

  closeAddModal(): void {
    this.showAddModal = false;
    this.newRoleName = '';
  }

  submitAddRole(): void {
    if (!this.newRoleName.trim()) {
      return;
    }

    const request: CreateRoleRequest = {
      name: this.newRoleName.trim()
    };

    this.roleService.createRole(request).subscribe({
      next: (role) => {
        this.roles.push(role);
        this.closeAddModal();
      },
      error: (error) => {
        this.error = 'Failed to create role';
        console.error('Error creating role:', error);
      }
    });
  }

  openEditModal(role: Role): void {
    this.editingRole = role;
    this.editRoleName = role.name;
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingRole = null;
    this.editRoleName = '';
  }

  submitEditRole(): void {
    if (!this.editingRole || !this.editRoleName.trim()) {
      return;
    }

    const request: UpdateRoleRequest = {
      name: this.editRoleName.trim()
    };

    this.roleService.updateRole(this.editingRole.id!, request).subscribe({
      next: (updatedRole) => {
        const index = this.roles.findIndex(r => r.id === updatedRole.id);
        if (index !== -1) {
          this.roles[index] = updatedRole;
        }
        this.closeEditModal();
      },
      error: (error) => {
        this.error = 'Failed to update role';
        console.error('Error updating role:', error);
      }
    });
  }

  openDeleteModal(role: Role): void {
    this.deletingRole = role;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deletingRole = null;
  }

  confirmDelete(): void {
    if (!this.deletingRole) {
      return;
    }

    this.roleService.deleteRole(this.deletingRole.id!).subscribe({
      next: () => {
        this.roles = this.roles.filter(r => r.id !== this.deletingRole!.id);
        this.closeDeleteModal();
      },
      error: (error) => {
        this.error = 'Failed to delete role';
        console.error('Error deleting role:', error);
      }
    });
  }
}
