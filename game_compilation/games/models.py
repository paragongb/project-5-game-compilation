from django.db import models
from django.urls import reverse
from django.utils.text import slugify


class Game(models.Model):
    title = models.CharField(max_length=100)
    slug = models.SlugField(max_length=110, unique=True, blank=True,
                             help_text="Auto-generated from the title if left blank.")
    description = models.TextField(blank=True)
    thumbnail = models.ImageField(upload_to='games/thumbnails/', blank=True, null=True)
    play_url = models.URLField(
        blank=True,
        help_text="Where 'Play' should take the user. Leave blank to link to the "
                   "game's detail page instead."
    )
    is_active = models.BooleanField(default=True, help_text="Uncheck to hide from the launcher.")
    order = models.PositiveIntegerField(default=0, help_text="Lower numbers appear first.")
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['order', 'title']

    def __str__(self):
        return self.title

    def save(self, *args, **kwargs):
        if not self.slug:
            base_slug = slugify(self.title)
            slug = base_slug
            counter = 1
            while Game.objects.filter(slug=slug).exclude(pk=self.pk).exists():
                counter += 1
                slug = f"{base_slug}-{counter}"
            self.slug = slug
        super().save(*args, **kwargs)

    def get_absolute_url(self):
        return reverse('games:game_detail', kwargs={'slug': self.slug})
