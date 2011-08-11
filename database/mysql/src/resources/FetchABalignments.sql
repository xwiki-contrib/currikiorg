/* Vault for older things
select rs.id from mycurrikidev_ab.xwiki_resource as rs where rs.resource_name="Coll_Group_MathForAmerica--Geometry.Lesson--RatiosandProportions"
select rs.id, align.topic_id, topic.guid, topic.label from mycurrikidev_ab.xwiki_resource as rs, mycurrikidev_ab.ab_resource_alignment as align,  mycurrikidev_ab.ab_topic as topic where rs.resource_name="Coll_Group_MathForAmerica--Geometry.Lesson--RatiosandProportions" and align.resource_id=rs.id and align.topic_id=topic.id
select rs.id, align.topic_id, topic.guid, topic.state_num, topic.stem, topic.text from mycurrikidev_ab.xwiki_resource as rs, mycurrikidev_ab.ab_resource_alignment as align,  mycurrikidev_ab.ab_topic as topic where rs.resource_name like "Coll_j%" and align.resource_id=rs.id and align.topic_id=topic.id
select rs.id, align.topic_id, topic.guid, topic.state_num, topic.stem, topic.text, standard.* from mycurrikidev_ab.xwiki_resource as rs, mycurrikidev_ab.ab_resource_alignment as align,  mycurrikidev_ab.ab_topic as topic, mycurrikidev_ab.ab_standard as standard where rs.resource_name like "Coll_j%" and align.resource_id=rs.id and align.topic_id=topic.id and standard.id=topic.standard_id
select rs.id, align.topic_id, topic.guid, topic.state_num, topic.stem, topic.text, standard.grade, standard.grade_code from mycurrikidev_ab.xwiki_resource as rs, mycurrikidev_ab.ab_resource_alignment as align,  mycurrikidev_ab.ab_topic as topic, mycurrikidev_ab.ab_standard as standard where rs.resource_name like "Coll_j%" and align.resource_id=rs.id and align.topic_id=topic.id and standard.id=topic.standard_id
*/
select topic.guid, topic.state_num, topic.stem, topic.text, standard.grade, standard.grade_code
  from
    mycurrikidev_ab.xwiki_resource as rs,
    mycurrikidev_ab.ab_resource_alignment as align,
    mycurrikidev_ab.ab_topic as topic,
    mycurrikidev_ab.ab_standard as standard
  where
    rs.resource_name=?
    and align.resource_id=rs.id
    and align.topic_id=topic.id
    and standard.id=topic.standard_id;

