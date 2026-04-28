import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../user.service';
import { UserBio } from '../../../shared/models/user/user-bio';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css',
})
export class EditProfile implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  form: FormGroup = this.fb.group({
    username:  ['', [Validators.required, Validators.minLength(5), Validators.maxLength(40)]],
    bioHeader: ['', Validators.maxLength(100)],
    bioBody:   ['', Validators.maxLength(1000)],
    bioFooter: ['', Validators.maxLength(100)],
    avatarUrl: [''],
  });

  saving = false;
  originalUsername: string = '';
  private userId: string | null = null;

  ngOnInit(): void {
    const username = this.route.snapshot.paramMap.get('id');
    if (!username) { this.router.navigate(['/home']); return; }

    this.userService.getUserByUsername(username).subscribe({
      next: user => {
        this.userId = user.id;
        this.form.patchValue({
          username:  user.username ?? '',
          bioHeader: user.bio?.header ?? '',
          bioBody:   user.bio?.body ?? '',
          bioFooter: user.bio?.footer ?? '',
          avatarUrl: user.avatarUrl ?? '',
        });
      },
      error: () => this.router.navigate(['/home']),
    });
  }

  save(): void {
    if (this.form.invalid || !this.userId) return;
    this.saving = true;

    const bio: UserBio = {
      header: this.form.value.bioHeader,
      body:   this.form.value.bioBody,
      footer: this.form.value.bioFooter,
    };

    this.userService.updateUser(this.userId, {
      username:  this.form.value.username,
      bio,
      avatarUrl: this.form.value.avatarUrl || null,
    }).subscribe({
      next: updated => this.router.navigate(['/profile', updated.username]),
      error: err => {
        console.error('Update failed:', err);
        this.saving = false;
      },
    });
  }
}
