(function(hbs) {
	hbs.registerHelper("debug", function(optionalValue) {
		console.log("====================");
		console.log(this);
		console.log("Value:" + optionalValue);
		console.log("====================");
	});
	hbs.registerHelper("cal", function(v1, operator, v2) {
		switch (operator) {
			case '+':
				return v1 + v2;
			case '-':
				return v1 - v2;
			case '*':
				return v1 * v2;
			case '/':
				return v1 / v2;
		}
	});
	Handlebars.registerHelper("fmtDist", function(value) {
		if(value<1000){
			return "<span class='hbs-value'>"+value+"</span><span class='hbs-unit'>m</span>";
		}
		if(value<100*1000){
			return "<span class='hbs-value'>"+value/1000+"</span><span class='hbs-unit'>km</span>";;
		}
		return "<span class='hbs-value'>"+Math.floor(value/1000)+"</span><span class='hbs-unit'>km</span>";;
	});
	hbs.registerHelper("compare", function(v1, operator, v2, options) {
		switch (operator) {
			case '==':
				return (v1 == v2) ? options.fn(this) : options.inverse(this);
				break;
			case '===':
				return (v1 === v2) ? options.fn(this) : options.inverse(this);
				break;
			case '<':
				return (v1 < v2) ? options.fn(this) : options.inverse(this);
				break;
			case '<=':
				return (v1 <= v2) ? options.fn(this) : options.inverse(this);
				break;
			case '>':
				return (v1 > v2) ? options.fn(this) : options.inverse(this);
				break;
			case '>=':
				return (v1 >= v2) ? options.fn(this) : options.inverse(this);
				break;
			default:
				return options.inverse(this);
				break;
		}
		return options.inverse(this);
	});
})(Handlebars);