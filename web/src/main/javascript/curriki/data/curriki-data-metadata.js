// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */


Ext.ns('Curriki.data.ict');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/instructional_component2  OR  Get filled in JS created by xwiki
Curriki.data.ict.list = ["activity_assignment","activity_exercise","activity_lab","activity_worksheet","activity_problemset","book_fiction","book_nonfiction","book_readings","book_textbook","curriculum_assessment","curriculum_course","curriculum_unit","curriculum_lp","curriculum_rubric","curriculum_scope","curriculum_standards","curriculum_studyguide","curriculum_syllabus","curriculum_tutorial","curriculum_workbook","resource_animation","resource_diagram","resource_glossary","resource_index","resource_photograph","resource_presentation","resource_collection","resource_script","resource_speech","resource_table","resource_template","resource_webcast","other"];
Curriki.data.ict.data = [ ];
Curriki.data.ict.list.each(function(ict){
	Curriki.data.ict.data.push([
		 ict
		,_('XWiki.AssetClass_instructional_component2_'+ict)
	]);
});
Curriki.data.ict.store = new Ext.data.SimpleStore({
	fields: ['id', 'ict'],
	data: Curriki.data.ict.data
});


Ext.ns('Curriki.data.el');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/educational_level2  OR  Get filled in JS created by xwiki
Curriki.data.el.list = [ "prek", "gr-k-2", "gr-3-5", "gr-6-8", "gr-9-10", "gr-11-12", "college_and_beyond", "professional_development", "special_education", "na" ];
Curriki.data.el.data = [ ];
Curriki.data.el.list.each(function(el){
	Curriki.data.el.data.push({
		 inputValue:el
		,boxLabel:_('XWiki.AssetClass_educational_level2_'+el)
	});
});


Ext.ns('Curriki.data.rights');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/rights  OR  Get filled in JS created by xwiki
Curriki.data.rights.list = [ "public", "members", "private" ];
Curriki.data.rights.initial = Curriki.data.rights.list[0];
Curriki.data.rights.data = [ ];
Curriki.data.rights.list.each(function(right){
	Curriki.data.rights.data.push({
		 inputValue:right
		,boxLabel:_('XWiki.AssetClass_rights_'+right)
		,checked:Curriki.data.rights.initial == right?true:false
	});
});


Ext.ns('Curriki.data.language');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/language  OR  Get filled in JS created by xwiki
Curriki.data.language.list = ["eng","ind","zho","nld","fra","deu","hin","ita","jpn","kor","nep","por","rus","sin","spa","999"];
Curriki.data.language.initial = Curriki.data.language.list[0];
Curriki.data.language.data = [ ];
Curriki.data.language.list.each(function(lang){
	Curriki.data.language.data.push([
		 lang
		,_('XWiki.AssetClass_language_'+lang)
	]);
});
Curriki.data.language.store = new Ext.data.SimpleStore({
	fields: ['id', 'language'],
	data: Curriki.data.language.data
});


Ext.ns('Curriki.data.licence');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetLicenseClass/fields/licenseType2  OR  Get filled in JS created by xwiki
Curriki.data.licence.list = [ "Licences.CurrikiLicense", "Licences.PublicDomain", "Licences.CreativeCommonsAttributionNon-Commercial", "Licences.CreativeCommonsAttributionNoDerivatives", "Licences.CreativeCommonsAttributionNon-CommercialNoDerivatives", "Licences.CreativeCommonsAttributionSharealike", "Licences.CreativeCommonsAttributionNon-CommercialShareAlike" ];
Curriki.data.licence.initial = Curriki.data.licence.list[0];
Curriki.data.licence.data = [ ];
Curriki.data.licence.list.each(function(lic){
	Curriki.data.licence.data.push([
		 lic
		,_('XWiki.AssetLicenseClass_licenseType2_'+lic)
	]);
});
Curriki.data.licence.store = new Ext.data.SimpleStore({
	fields: ['id', 'licence'],
	data: Curriki.data.licence.data
});


Ext.ns('Curriki.data.fw_item');
// For fwTree
Curriki.data.fw_item.fwCheckListener = function(node, checked){
	var validator = Ext.getCmp('fw_items-validation');
	if (validator) {
		validator.setValue(validator.getValue()+(checked?1:-1));
	}
	if (checked){
		if ("undefined" !== typeof node.parentNode){
			if (!node.parentNode.ui.isChecked()){
				node.parentNode.ui.toggleCheck();
			}
		}
	} else {
		if (Ext.isArray(node.childNodes)){
			node.childNodes.each(function(node){
				if (node.ui.isChecked()) {
					node.ui.toggleCheck();
				}
			});
		}
	}
};

// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetLicenseClass/fields/fw_items  OR  Get filled in JS created by xwiki
Curriki.data.fw_item.fwMap = {"TREEROOTNODE":[{"id":"FW_masterFramework.WebHome","parent":""}],"FW_masterFramework.WebHome":[{"id":"FW_masterFramework.Arts","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.VocationalEducation","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Education&Teaching","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.EducationalTechnology","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Health","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Information&MediaLiteracy","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.LanguageArts","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Mathematics","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Science","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.SocialStudies","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.ForeignLanguages","parent":"FW_masterFramework.WebHome"}],"FW_masterFramework.Information&MediaLiteracy":[{"id":"FW_masterFramework.EvaluatingSources","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.MediaEthics","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.OnlineSafety","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.ResearchMethods","parent":"FW_masterFramework.Information&MediaLiteracy"}],"FW_masterFramework.SocialStudies":[{"id":"FW_masterFramework.Anthropology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Careers_5","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Civics","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.CurrentEvents","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Economics","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Entrepreneurship","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Geography","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.GlobalAwareness","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Government","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.History Local","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.PoliticalSystems","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Psychology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Religion","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Research_0","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Sociology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.StateHistory","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Technology_1","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Thinking&ProblemSolving","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.UnitedStatesGovernment","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.UnitedStatesHistory","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.WorldHistory","parent":"FW_masterFramework.SocialStudies"}],"FW_masterFramework.Arts":[{"id":"FW_masterFramework.Architecture","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Careers","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Dance","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.DramaDramatics","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Film","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.History","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Music","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Photography","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.PopularCulture","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Technology","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.VisualArts","parent":"FW_masterFramework.Arts"}],"FW_masterFramework.EducationalTechnology":[{"id":"FW_masterFramework.Careers_0","parent":"FW_masterFramework.EducationalTechnology"},{"id":"FW_masterFramework.IntegratingTechnologyintotheClassroom","parent":"FW_masterFramework.EducationalTechnology"},{"id":"FW_masterFramework.UsingMultimedia&theInternet","parent":"FW_masterFramework.EducationalTechnology"}],"FW_masterFramework.VocationalEducation":[{"id":"FW_masterFramework.Agriculture_0","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Business","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Careers_6","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.OccupationalHomeEconomics","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.School-to-work","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Technology_2","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Trade&Industrial","parent":"FW_masterFramework.VocationalEducation"}],"FW_masterFramework.Health":[{"id":"FW_masterFramework.BodySystems&Senses","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.Careers_1","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.EnvironmentalHealth","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.HumanSexuality","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.MentalEmotionalHealth","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.Nutrition","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.SafetySmokingSubstanceAbusePrevention","parent":"FW_masterFramework.Health"}],"FW_masterFramework.Education&Teaching":[{"id":"FW_masterFramework.Accessibility","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.AdultEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.BilingualEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.ClassroomManagement","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EarlyChildhoodEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationAdministration","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationalFoundations","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationalPsychology","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.InstructionalDesign","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.MeasurementEvaluation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.Mentoring","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.MulticulturalEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.StandardsAlignment","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.TeachingTechniques","parent":"FW_masterFramework.Education&Teaching"}],"FW_masterFramework.ForeignLanguages":[{"id":"FW_masterFramework.Alphabet","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Careers_7","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.CulturalAwareness","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Grammar","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.InformalEducation","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Linguistics","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.ListeningComprehension","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Reading","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Speaking","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Spelling","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.VocabularyWriting","parent":"FW_masterFramework.ForeignLanguages"}],"FW_masterFramework.Mathematics":[{"id":"FW_masterFramework.Algebra","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Appliedmathematics","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Arithmetic","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Calculus","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Careers_3","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.DataAnalysis&Probability","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Equations","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Estimation","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Geometry","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Graphing","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Measurement","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.NumberSense&Operations","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Patterns","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.ProblemSolving","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Statistics","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Trigonometry","parent":"FW_masterFramework.Mathematics"}],"FW_masterFramework.Science":[{"id":"FW_masterFramework.Agriculture","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Astronomy","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Biology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Botany","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Careers_4","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Chemistry","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Earthscience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Ecology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Engineering","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Generalscience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Geology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.HistoryofScience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.LifeSciences","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Meteorology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.NaturalHistory","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Oceanography","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Paleontology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.PhysicalSciences","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Physics","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.ProcessSkills","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Technology_0","parent":"FW_masterFramework.Science"}],"FW_masterFramework.LanguageArts":[{"id":"FW_masterFramework.Alphabet_0","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Careers_2","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Journalism","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Listening&Speaking","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Literature","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Phonics","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.ReadingComprehension","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Research","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Spelling_0","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.StoryTelling","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Vocabulary","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Writing","parent":"FW_masterFramework.LanguageArts"}]};
var fwItem = 'FW_masterFramework.WebHome';
Curriki.data.fw_item.fwAddNode = function(fwMap, nodeName){
	var nodeInfo = {
		 id:nodeName
		,text:_('XWiki.AssetClass_fw_items_'+nodeName)
		,checked:false
		,listeners:{
			checkchange: Curriki.data.fw_item.fwCheckListener
		}
	};
	if ("undefined" !== typeof fwMap[nodeName]){
		var children = [];
		fwMap[nodeName].each(function(childNode){
			children.push(Curriki.data.fw_item.fwAddNode(fwMap, childNode.id));
		});
		nodeInfo.children = children;
		nodeInfo.cls = 'fw-item fw-item-parent';
	} else {
		nodeInfo.leaf = true;
		nodeInfo.cls = 'fw-item fw-item-bottom';
	}

	return nodeInfo;
};
Curriki.data.fw_item.fwChildren = Curriki.data.fw_item.fwAddNode(Curriki.data.fw_item.fwMap, 'FW_masterFramework.WebHome').children;
Ext.ns('Curriki.ui.component.asset');
Curriki.ui.component.asset.getFwTree = function(){
	return {
		 xtype:'treepanel'
		,loader: new Ext.tree.TreeLoader({
			preloadChildren:true
		})
		,id:'fw_items-tree'
		,useArrows:true
		,autoHeight:true
		,border:false
		,cls:'fw-tree'
		,animate:true
		,enableDD:false
		,containerScroll:true
		,rootVisible:true
		,root: new Ext.tree.AsyncTreeNode({
			 text:_('XWiki.AssetClass_fw_items_FW_masterFramework.WebHome')
			,id:'FW_masterFramework.WebHome'
			,cls:'fw-item-top fw-item-parent fw-item'
			,leaf:false
			,expanded:true
			,children:Curriki.data.fw_item.fwChildren
		})
	};
};
