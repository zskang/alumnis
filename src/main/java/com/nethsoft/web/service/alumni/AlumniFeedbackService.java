package com.nethsoft.web.service.alumni;

import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniFeedback;
import com.nethsoft.web.service.BaseService;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniFeedbackService extends BaseService<AlumniFeedback> {

    public List<AlumniFeedback> find(PageBean pageBean){
        List<AlumniFeedback> list = super.listByPage(pageBean);
        if(list != null && list.size()>0){
            for(AlumniFeedback alumniFeedback : list){
                Hibernate.initialize(alumniFeedback.getCampusStudent());
            }
        }
        return list;
    }
}
