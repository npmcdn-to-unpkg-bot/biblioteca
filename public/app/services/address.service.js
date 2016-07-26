angular.module('architectplay')
    .service('Usuario',['$resource', 'BaseUrl',
      function($resource, BaseUrl){
        return $resource(BaseUrl + '/usuario/:id', {}, {
            cadastrar: {method: 'POST', url: BaseUrl + '/usuario/cadastrar', isArray: false},
            update: {method: 'PUT', url: BaseUrl + '/usuario/:id', isArray: false},
            getAll: {method: 'GET', url: BaseUrl + '/usuarios', isArray: true},
            reset: {method: 'POST', url: BaseUrl + '/reset/senha', isArray: false},
            getFiltroUsuarios: {method: 'GET', url: BaseUrl + '/usuarios/filtro/:filtro', isArray: true},
            getAutenticado: {method: 'GET', url: BaseUrl + '/current', isArray: false}
        });
    }]).service('Contato',['$resource', 'BaseUrl',
      function($resource, BaseUrl){
        return $resource(BaseUrl + '/contato/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/contatos', isArray: true},
            getFiltroContatos: {method: 'GET', url: BaseUrl + '/contatos/filtro/:filtro', isArray: true}
        });
    }]).service('Artigo',['$resource', 'BaseUrl',
      function($resource, BaseUrl){
        return $resource(BaseUrl + '/artigo/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/artigos', isArray: true},
            getFiltroArtigos: {method: 'GET', url: BaseUrl + '/artigos/filtro/:filtro', isArray: true}
        });
    }]).service('Livro',['$resource', 'BaseUrl',
      function($resource, BaseUrl){
        return $resource(BaseUrl + '/livro/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/livros', isArray: true},
            getFiltroLivros: {method: 'GET', url: BaseUrl + '/livros/filtro/:filtro', isArray: true}
        });
    }]).service('Evento',['$resource', 'BaseUrl',
    function($resource, BaseUrl){
        return $resource(BaseUrl + '/evento/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/eventos', isArray: true},
            getFiltroEventos: {method: 'GET', url: BaseUrl + '/eventos/filtro/:filtro', isArray: true}
        });
    }]).service('Video',['$resource', 'BaseUrl',
    function($resource, BaseUrl){
        return $resource(BaseUrl + '/video/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/videos', isArray: true}
        });
    }]).service('Curso',['$resource', 'BaseUrl',
    function($resource, BaseUrl){
        return $resource(BaseUrl + '/curso/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/cursos', isArray: true}
        });
    }]).service('Noticia',['$resource', 'BaseUrl',
    function($resource, BaseUrl){
        return $resource(BaseUrl + '/noticia/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/noticias', isArray: true}
        });
    }]).service('Publicacao',['$resource', 'BaseUrl',
    function($resource, BaseUrl){
        return $resource(BaseUrl + '/publicacao/:id', {}, {
            getAll: {method: 'GET', url: BaseUrl + '/publicacoes', isArray: true}
        });
    }]);