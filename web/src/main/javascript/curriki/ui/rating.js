// vim: ts=4:sw=4

/*
 * Based on Ext.ux.form.Rater (http://extjs.com/forum/showthread.php?t=10822)
 */

/*global Ext */
/*global Curriki */
/*global _ */

(function(){
Ext.ns('Curriki.ui.Rating');
var rating_list = [];

for (var i=0; i<5; i++){
	rating_list.push(_('CurrikiCode.AssetClass_member_rating_'+(i+1)));
}

Curriki.ui.Rating = Ext.extend(Ext.form.NumberField, {
	fieldClass : 'x-form-field x-form-rating-field',

	allowDecimals : false,

	allowNegative : false,

	minValue : 0,

	maxValue : 5,

	// For Rating
	unit : 17,
	wrapClass : 'ux-form-rater-wrap',
	starsClass : 'ux-form-rater-stars',
	hoverClass : 'ux-form-rater-hover',
	voteClass : 'ux-form-rater-vote',
	votedClass : 'ux-form-rater-voted',
	textRightClass : 'ux-form-rater-text-right',
	hoverText: rating_list,

	// private config
	displayValue : undefined,
	ratedValue : undefined,
	hoverValue : undefined,

	rated : false,



	// Methods

	initComponent : function(){
		Curriki.ui.Rating.superclass.initComponent.call(this);
		this.addEvents(
			'beforerating',
			'rate'
		);
	},

	// private
	onRender : function(ct, position){
		Curriki.ui.Rating.superclass.onRender.apply(this, arguments);

		this.wrap = this.el.wrap({cls: this.wrapClass});
		if(Ext.isIE) this.wrap.setHeight(this.unit);

		// TODO: Needed?  fix for ie using in dynamic form
		this.el.addClass('x-hidden');

		this.createStars();
		this.createTextContainers();

		this.displayValue = (this.displayValue > this.maxValue) ? this.maxValue : this.displayValue;

		if (this.displayValue > 0 || this.getValue() > 0){
			this.displayRating();
		}
	},

	// private
	initEvents: function(){
		Curriki.ui.Rating.superclass.initEvents.call(this);

		var ct = this.getStarsContainer();
		var stars = this.getStars();

		stars.on('mouseover', this.displayHover, this);
		stars.on('mouseout', this.removeHover, this);
		stars.on('click', this.rate, this);
		stars.on('mouseup', this.rate, this);
	},

	// private
	displayHover: function(e){
		if (this.disabled) return;

		var target = Ext.get(e.getTarget());
		target.addClass(this.hoverClass);

		var rating = this.getRating();
		rating.hide();

		var stars = this.getStars();
		var i = 0;
		while (stars.item(i) != null){
			if (stars.item(i) == target) {
				this.hoverValue = this.maxValue - i;
				if (this.hoverText instanceof Array){
					if (!Ext.isEmpty(this.hoverText[this.hoverValue-1])) {
						this.setRightText(this.hoverText[this.hoverValue-1]);
					}
				}
				return;
			}
			i++;
		}
	},

	// private
	removeHover: function(e){
		if (this.disabled) return;

		var rating = this.getRating();
		rating.show();

		var el = e.getTarget();
		Ext.fly(el).removeClass(this.hoverClass);

		this.setRightText('');
	},

	// private
	rate: function(e){
		if (this.disabled) return;

		var hv = this.hoverValue;
		this.setValue(hv);
		if (this.fireEvent('beforerating', this) === false){
			return;
		}

		this.removeHover(e);
		this.onBlur();
		this.rated = true;
		this.displayRating(hv);

		this.fireEvent('rate', this, hv);
	},

	// private
	createStars: function() {
		if (this.getStars().getCount() > 0){
			return;
		}

		var ul = this.wrap.createChild({tag:'ul', cls:this.starsClass}).setSize(this.unit*this.maxValue, this.unit);

		// append to rating container
		var tplr = new Ext.Template('<li class="rating"></li>');
		var tpls = new Ext.Template('<li class="star"></li>');

		tplr.append(ul, [], true).setHeight(this.unit);

		for (var i=this.maxValue; i>0; i--){
			var star = tpls.append(ul, [], true);
			star.setSize(this.unit*i, this.unit);
		}

		this.alignStars();
	},

	// private
	createTextContainers: function() {
		var ct = this.getStarsContainer();

		if (!this.textRightContainer) {
			this.textRightContainer = Ext.DomHelper.insertAfter(ct, {tag:"span", cls:this.textRightClass}, true);
			this.textRightContainer.addClass('x-hidden');
		}
	},

	setRightText: function(t){
		this.textRightContainer.dom.innerHTML = t;
		if (t == null || t == '') {
			this.textRightContainer.addClass('x-hidden');
		} else {
			this.textRightContainer.removeClass('x-hidden');
		}
	},

	getRightText: function(){
		return this.textRightContainer.dom.innerHTML;
	},

	displayRating: function(v, finalRating) {
		var el = this.getRating();

		if (Ext.isEmpty(v)) {
			v = (this.displayValue == null)?this.getValue():this.displayValue;
			v = Ext.isEmpty(v)?0:v;
		}

		if (this.ratedValue > 0){
			v = this.ratedValue;
			this.rated = true;
		}

		var replaceClass = function(vtd, vt){
			if(finalRating == true){
				el.replaceClass(vt, vtd);
			} else {
				el.replaceClass(vtd, vt);
			}
		}

		replaceClass(this.votedClass, this.voteClass);
		el.setWidth(v*this.unit);
		return;
	},

	// private
	getStars: function() {
		return this.wrap.select('li.star', true);
	},

	// private
	getStarsContainer: function() {
		return this.wrap.select('.'+this.starsClass, true).item(0);
	},

	// private
	getRating: function(){
		return this.wrap.select('li.rating', true);
	},

	// private
	alignStars: function() {
		var ct = this.getStarsContainer();
		var rating = this.getRating();
		var stars = this.getStars();

		var isInForm = (ct.findParent('.x-form-item', 5))?true:false;

		if (false && !isInForm){
			var leftOffset = Ext.fly(document.body).getAlignToXY(ct)[0];
			rating.setLeft(leftOffset);
			stars.setLeft(leftOffset);
		} else {
			rating.alignTo(ct, 'tl');
			stars.alignTo(ct, 'tl');
		}
	},

	// private
	onDisable : function(){
		Curriki.ui.Rating.superclass.onDisable.call(this);
		this.wrap.addClass('x-item-disabled');
	},

	// private
	onEnable : function(){
		Curriki.ui.Rating.superclass.onEnable.call(this);
		this.wrap.removeClass('x-item-disabled');
	},
	
	// private
	onHide : function(){
		this.wrap.addClass('x-hidden');
	},

	// private
	onShow : function(){
		this.wrap.removeClass('x-hidden');
	}
});
Ext.reg('rating', Curriki.ui.Rating);
})();
