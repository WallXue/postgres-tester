package com.cenboomh.dao.write;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WriteMapper {
    @Select("INSERT INTO \"emr_test\".\"emr_medicalrecord\" ( \"id\", \"name\", \"patient_id\", \"app_id\", \"docxml\", \"status\", \"issign\", \"dochistory\", \"locked\", \"locktime\", \"locker\", \"createtime\", \"creator\", \"lastedittime\", \"lastediter\", \"displaytime\", \"printtimes\", \"isnewpage\", \"doclead\", \"template_id\", \"auditor\", \"encounter_id\", \"medicalrecordtype_id\", \"dept_id\", \"firstsigntime\", \"ward_id\", \"bed_code\", \"patient_visit_id\", \"visit\", \"patient_type\", \"sign_status\", \"docxml_lob_id\", \"comments\", \"pdf_create_time\", \"pdf_auto_gen_time\", \"revision_num\", \"group_status\", \"u_time\" ) VALUES ( #{id}, '护理评估表(成人)', '01656702', NULL, NULL, '0', '1', NULL, '1', '2020-08-10 00:00:00', '200152', '2020-07-28 17:07:09', '200152', '2020-07-29 10:36:01', '200193', '2020-07-28 17:06:00', NULL, 0, '200152', 61989867, '200152', 19948, '2#16#840#1#113883#13#42#1#1#403939', '010112012802', NULL, NULL, NULL, 19948, 1, '2', '3', '0120200728004837', NULL, NULL, NULL, 0, NULL, NULL );")
    String testInsert(Long id);


    @Select("INSERT INTO \"emr_test\".\"big_field_test\"(\"id\", \"big_field\") VALUES (#{id}, #{bigField});")
    String testBigFieldInsert(Long id, String bigField);
}
