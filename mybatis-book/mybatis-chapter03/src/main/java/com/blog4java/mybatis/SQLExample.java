package com.blog4java.mybatis;

import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLExample {
    /**
     * 测试
     * 1. SELECT、FROM、INNER_JOIN 可以多次使用
     * 2. where可以多次使用 默认会用and连接
     * 3. ORDER_BY可以多次使用会用逗号连接
     *
     *
     */

    @Test
    public void testSelectSQL() {
        String orgSql = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" +
                        "FROM PERSON P, ACCOUNT A\n" +
                        "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n" +
                        "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n" +
                        "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n" +
                        "OR (P.LAST_NAME like ?)\n" +
                        "GROUP BY P.ID\n" +
                        "HAVING (P.LAST_NAME like ?) \n" +
                        "OR (P.FIRST_NAME like ?)\n" +
                        "ORDER BY P.ID, P.FULL_NAME";

        String newSql =  new SQL() {{
                    SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
                    SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
                    FROM("PERSON P");
                    FROM("ACCOUNT A");
                    INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
                    INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
                    WHERE("P.ID = A.ID");
                    WHERE("P.FIRST_NAME like ?");
                    OR();
                    WHERE("P.LAST_NAME like ?");
                    GROUP_BY("P.ID");
                    HAVING("P.LAST_NAME like ?");
                    OR();
                    HAVING("P.FIRST_NAME like ?");
                    ORDER_BY("P.ID");
                    ORDER_BY("P.FULL_NAME");
                }}.toString();

        assertEquals(orgSql, newSql);
    }

    @Test
    public void testDynamicSQL() {
        String expSql ="SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n"
                +"FROM PERSON P\n"
                +"ORDER BY P.LAST_NAME";
        String sql = selectPerson(null, null, null);
        assertEquals(expSql,sql);
    }

    public String selectPerson(final String id, final String firstName, final String lastName) {
        return new SQL() {{
            SELECT("P.ID, P.USERNAME, P.PASSWORD");
            SELECT("P.FIRST_NAME, P.LAST_NAME");
            FROM("PERSON P");
            if (id != null) {
                WHERE("P.ID = #{id}");
            }
            if (firstName != null) {
                WHERE("P.FIRST_NAME = #{firstName}");
            }
            if (lastName != null) {
                WHERE("P.LAST_NAME = #{lastName}");
            }
            ORDER_BY("P.LAST_NAME");
        }}.toString();
    }

    @Test
    public  void testInsertSql() {
        String expSql ="INSERT INTO PERSON\n (ID, FIRST_NAME, LAST_NAME)\n"
                +"VALUES (#{id}, #{firstName}, #{lastName})";
        String insertSql = new SQL().
            INSERT_INTO("PERSON").
            VALUES("ID, FIRST_NAME", "#{id}, #{firstName}").
            VALUES("LAST_NAME", "#{lastName}").toString();
        assertEquals(expSql,insertSql);
    }

    @Test
    public void  testDeleteSql() {
        String expSql ="DELETE FROM PERSON\n"
                +"WHERE (ID = #{id})";
        String deleteSql =  new SQL() {{
            DELETE_FROM("PERSON");
            WHERE("ID = #{id}");
        }}.toString();
        assertEquals(expSql,deleteSql);
    }

    @Test
    public void testUpdateSql() {
        String expSql ="UPDATE PERSON\n"
                +"SET FIRST_NAME = #{firstName}\n"
                +"WHERE (ID = #{id})";
        String updateSql =  new SQL() {{
            UPDATE("PERSON");
            SET("FIRST_NAME = #{firstName}");
            WHERE("ID = #{id}");
        }}.toString();
        assertEquals(expSql,updateSql);
    }


}
