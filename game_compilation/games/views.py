from django.shortcuts import get_object_or_404, render

from .models import Game


def home(request):
    games = Game.objects.filter(is_active=True)
    return render(request, 'games/home.html', {'games': games})


def game_detail(request, slug):
    game = get_object_or_404(Game, slug=slug, is_active=True)
    if game.title.strip().lower() == 'chess':
        return render(request, 'games/chess.html', {'game': game})
    return render(request, 'games/game_detail.html', {'game': game})
