package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.BaseDao;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mybatis.*;
import net.thumbtack.school.buscompany.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.TransactionIsolationLevel;

public class BaseDaoImpl implements BaseDao {
    protected final String path = "net.thumbtack.school.buscompany.mapper.mybatis.";

    protected SqlSession getSession(){
        return MyBatisUtils.getSqlSessionFactory().openSession();
    }

    protected SqlSession getSerializableSession(){
        return MyBatisUtils.getSqlSessionFactory().openSession(TransactionIsolationLevel.SERIALIZABLE);
    }

    protected AdminMybatisMapper getAdminMapper(SqlSession session){
        return session.getMapper(AdminMybatisMapper.class);
    }

    protected ClientMybatisMapper getClientMapper(SqlSession session){
        return session.getMapper(ClientMybatisMapper.class);
    }

    protected UserMybatisMapper getUserMapper(SqlSession session){
        return session.getMapper(UserMybatisMapper.class);
    }

    protected BusMybatisMapper getBusMapper(SqlSession session){
        return session.getMapper(BusMybatisMapper.class);
    }

    protected TripMybatisMapper getTripMapper(SqlSession session){
        return session.getMapper(TripMybatisMapper.class);
    }

    protected OrderMybatisMapper getOrderMapper(SqlSession session){
        return session.getMapper(OrderMybatisMapper.class);
    }

    @Override
    public void clearAll() {
        try(SqlSession session = getSession()){
            try{
                getUserMapper(session).clear();
                getBusMapper(session).clear();
                getTripMapper(session).clear();
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new ServerException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }
}
