
function formatState (state) {
	if(state.selected !==undefined){
		return state.name;
	}
	  if (!state.id) {
	    return state.text;
	  }

	  var $state = $(
	    '<div><i class="fa fa-paper-plane" aria-hidden="true"></i> <span></span> <ul class="ulresult"></ul></div>'
	  );

	  // Use .text() instead of HTML string concatenation to avoid script injection issues
	  $state.find("span").text(state.date);
	 var list = state.name.split(',').map(function(item){
		 return "<li>"+item.split(";")[0]+"= "+item.split(";")[1]+"</li>";
	 })
	  $state.find("ul").html(list);
	  return $state;
	};
	$('.js-data-example-ajax').select2({
		  theme: 'bootstrap4',
		templateResult: formatState,
		templateSelection: formatState,
	  ajax: {
	    url: 'versions',
	    dataType: 'json',
	    processResults: function (data) {
	        return {
	          results: data
	        };
	      }
	  }
	});