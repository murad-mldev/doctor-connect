import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Users,
  Plus,
  Edit2,
  Trash2,
  X,
  Check,
  Shield,
} from 'lucide-angular';
import { UserService } from '../../../services/user.service';
import { RoleService } from '../../../services/role.service';
import { User, Role } from '../../../models';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  readonly Users = Users;
  readonly Plus = Plus;
  readonly Edit2 = Edit2;
  readonly Trash2 = Trash2;
  readonly X = X;
  readonly Check = Check;
  readonly Shield = Shield;

  users: User[] = [];
  allRoles: Role[] = [];
  isLoading = false;
  error: string | null = null;

  // Modal state
  showEditModal = false;
  showDeleteModal = false;
  showRoleModal = false;

  // Form data
  editingUser: User | null = null;
  editUserData: Partial<User> = {};
  deletingUser: User | null = null;

  // Role assignment
  assigningRolesUser: User | null = null;
  selectedRoleIds: Set<string> = new Set();

  constructor(
    private userService: UserService,
    private roleService: RoleService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.error = null;

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load users';
        console.error('Error loading users:', error);
        this.isLoading = false;
      },
    });
  }

  loadRoles(): void {
    this.roleService.getAllRoles().subscribe({
      next: (roles) => {
        this.allRoles = roles;
      },
      error: (error) => {
        console.error('Error loading roles:', error);
      },
    });
  }

  openEditModal(user: User): void {
    this.editingUser = user;
    this.editUserData = {
      emailOrPhoneNumber: user.emailOrPhoneNumber,
      fullName: user.fullName,
      email: user.email,
      phone: user.phone,
      isActive: user.isActive ?? true,
      isVerified: user.isVerified ?? false,
    };
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingUser = null;
    this.editUserData = {};
  }

  submitEditUser(): void {
    if (!this.editingUser) {
      return;
    }

    // Ensure boolean values are properly set
    const updatePayload = {
      ...this.editUserData,
      isActive: !!this.editUserData.isActive,
      isVerified: !!this.editUserData.isVerified,
    };

    this.userService.updateUser(this.editingUser.id!, updatePayload).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex((u) => u.id === updatedUser.id);
        if (index !== -1) {
          this.users = [
            ...this.users.slice(0, index),
            updatedUser,
            ...this.users.slice(index + 1),
          ];
        }
        this.closeEditModal();
      },
      error: (error) => {
        this.error = 'Failed to update user';
        console.error('Error updating user:', error);
      },
    });
  }

  openDeleteModal(user: User): void {
    this.deletingUser = user;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deletingUser = null;
  }

  confirmDelete(): void {
    if (!this.deletingUser) {
      return;
    }

    this.userService.deleteUser(this.deletingUser.id!).subscribe({
      next: () => {
        this.users = this.users.filter((u) => u.id !== this.deletingUser!.id);
        this.closeDeleteModal();
      },
      error: (error) => {
        this.error = 'Failed to delete user';
        console.error('Error deleting user:', error);
      },
    });
  }

  openRoleModal(user: User): void {
    this.assigningRolesUser = user;
    this.selectedRoleIds = new Set(user.roles?.map((r) => r.id!) || []);
    this.showRoleModal = true;
  }

  closeRoleModal(): void {
    this.showRoleModal = false;
    this.assigningRolesUser = null;
    this.selectedRoleIds = new Set();
  }

  toggleRole(roleId: string): void {
    if (this.selectedRoleIds.has(roleId)) {
      this.selectedRoleIds.delete(roleId);
    } else {
      this.selectedRoleIds.add(roleId);
    }
  }

  isRoleSelected(roleId: string): boolean {
    return this.selectedRoleIds.has(roleId);
  }

  submitRoleAssignment(): void {
    if (!this.assigningRolesUser) {
      return;
    }

    const selectedRoles = this.allRoles.filter((r) =>
      this.selectedRoleIds.has(r.id!)
    );

    this.userService
      .updateUser(this.assigningRolesUser.id!, { roles: selectedRoles })
      .subscribe({
        next: (updatedUser) => {
          const index = this.users.findIndex((u) => u.id === updatedUser.id);
          if (index !== -1) {
            this.users[index] = updatedUser;
          }
          this.closeRoleModal();
        },
        error: (error) => {
          this.error = 'Failed to update user roles';
          console.error('Error updating user roles:', error);
        },
      });
  }

  getUserRoleNames(user: User): string {
    return user.roles?.map((r) => r.name).join(', ') || 'No roles';
  }

  getStatusBadgeClass(user: User): string {
    return user.isActive ? 'badge-success' : 'badge-danger';
  }

  getStatusText(user: User): string {
    console.log(user.isActive);
    return user.isActive ? 'Active' : 'Inactive';
  }
}
