angular.module('architectplay')
    .service('Usuario',['$resource',
      function($resource){
        return $resource('usuario/:id', {}, {
            cadastrar: {method: 'POST', url: 'usuario/cadastrar', isArray: false},
            update: {method: 'PUT', url: 'usuario/:id', isArray: false},
            getAll: {method: 'GET', url: 'usuarios', isArray: true},
            reset: {method: 'POST', url: 'reset/senha', isArray: false},
            getFiltroUsuarios: {method: 'GET', url: 'usuarios/filtro/:filtro', isArray: true},
            getAutenticado: {method: 'GET', url: 'current', isArray: false}
        });
    }]).service('Contato',['$resource',
      function($resource){
        return $resource('contato/:id', {}, {
            getAll: {method: 'GET', url: 'contatos', isArray: true},
            getFiltroContatos: {method: 'GET', url: 'contatos/filtro/:filtro', isArray: true}
        });
    }]).service('Artigo',['$resource',
      function($resource){
        return $resource('artigo/:id', {}, {
            getAll: {method: 'GET', url: 'artigos', isArray: true},
            getFiltroArtigos: {method: 'GET', url: 'artigos/filtro/:filtro', isArray: true}
        });
    }]).service('Livro',['$resource',
      function($resource){
        return $resource('livro/:id', {}, {
            getAll: {method: 'GET', url: 'livros', isArray: true},
            getFiltroLivros: {method: 'GET', url: 'livros/filtro/:filtro', isArray: true}
        });
    }]).service('Evento',['$resource',
        function($resource){
            return $resource('evento/:id', {}, {
                getAll: {method: 'GET', url: 'eventos', isArray: true},
                getFiltroEventos: {method: 'GET', url: 'eventos/filtro/:filtro', isArray: true}
            });
    }]).service('Video',['$resource',
        function($resource){
            return $resource('video/:id', {}, {
                getAll: {method: 'GET', url: 'videos', isArray: true}
            });
    }]).service('Curso',['$resource',
        function($resource){
            return $resource('curso/:id', {}, {
                getAll: {method: 'GET', url: 'cursos', isArray: true}
            });
    }]).service('Noticia',['$resource',
        function($resource){
            return $resource('noticia/:id', {}, {
                getAll: {method: 'GET', url: 'noticias', isArray: true}
            });
    }]).service('Publicacao',['$resource',
        function($resource){
            return $resource('publicacao/:id', {}, {
                getAll: {method: 'GET', url: 'publicacoes', isArray: true}
            });
    }]);