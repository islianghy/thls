package com.p2b.front.dao;

import com.p2b.front.entity.Custom;

public interface CustomMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table custom
     *
     * @mbggenerated Fri Jan 26 13:46:48 CST 2018
     */
    int deleteByPrimaryKey(Long customId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table custom
     *
     * @mbggenerated Fri Jan 26 13:46:48 CST 2018
     */
    int insert(Custom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table custom
     *
     * @mbggenerated Fri Jan 26 13:46:48 CST 2018
     */
    int insertSelective(Custom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table custom
     *
     * @mbggenerated Fri Jan 26 13:46:48 CST 2018
     */
    Custom selectByPrimaryKey(Long customId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table custom
     *
     * @mbggenerated Fri Jan 26 13:46:48 CST 2018
     */
    int updateByPrimaryKeySelective(Custom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table custom
     *
     * @mbggenerated Fri Jan 26 13:46:48 CST 2018
     */
    int updateByPrimaryKey(Custom record);
}