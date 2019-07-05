(function() {
    var host = ehpyxunknwssmdcjjvqvbwudgypaxwoehdasujkbi;
    /*
    Too intrusive ... for example does not run google analytics! Also a bit sensitive to internals

    if(typeof addOpenSearch === 'function') {
        addOpenSearch = function(name,ext,cat,pid,meth) {
            var url = "https://mycroftproject.com/installos.php/" + pid + "/" + name + ".xml";
            host.add(url);
    }
    }*/

    if(!('external' in window)) {
        window.external = {}
    }
    window.external.AddSearchProvider = function(url) {
        host.add(url);
    }

    window.chrome = undefined;
    try {
        delete window.chrome;
    } catch(e){}
})();