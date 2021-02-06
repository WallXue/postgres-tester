package com.cenboomh.dao.read;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReadMapper {
    @Select("SELECT T\n" +
            "\t.patient_type,\n" +
            "\tCOUNT ( 0 ) AS sync_data_num \n" +
            "FROM\n" +
            "\temr_test.emr_medicalrecord\n" +
            "\tT JOIN emr.emr_medicalrecordtype t1 ON T.medicalrecordtype_id = t1.\n" +
            "\tID LEFT JOIN emr_test.emr_template t3 ON T.template_id = t3.\n" +
            "\tID\n" +
            "\tAND T.patient_type = '1'\n" +
            "GROUP BY\n" +
            "\tT.patient_type")
    List<Map> testQuery(String p1, String p2, String p3, String p4);

    @Select("select * from emr_test.emr_medicalrecord where id>floor(random()*(1000000-0))\n" +
            "and id < floor(random()*(1000000-0))\n" +
            "limit floor(random()*(100-0))+1;")
    @Options(statementType = StatementType.STATEMENT)
    List<Map> testRandomQuery();

//    where id>floor(random()*(1000000-0)) and id < floor(random()*(1000000-0))
    @Select("select * from emr_test.big_field_test limit floor(random()*(10-0))+1;")
    @Options(statementType = StatementType.STATEMENT)
    List<Map> testBigFieldQuery();



}
