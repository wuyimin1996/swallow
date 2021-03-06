package com.cy.example.controller.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cy.example.carrier.User_Role_Ca;
import com.cy.example.controller.BaseController;
import com.cy.example.model.Page;
import com.cy.example.model.Result;
import com.cy.example.service.IUserService;
import com.cy.example.service.IUser_RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/user_role")
public class User_RoleController extends BaseController {

	@Autowired
	private IUser_RoleService urService;

	@Autowired
	private IUserService userService;
	
	@PostMapping
	public Result<String> add(@ModelAttribute("ur") User_Role_Ca ur) {
		ur.setN_userId(Long.valueOf(ur.getC_username()));
		ur.setN_roleId(Long.valueOf(ur.getC_roleName()));
		Map<String, Object> map = new HashMap<String, Object>();
		User_Role_Ca user = urService.selectOne(new EntityWrapper<User_Role_Ca>().eq("n_userId", ur.getN_userId()));
		String msg;
		boolean flag;
		if(null == user){
			flag = urService.insert(ur);
			//刷新缓存
			userService.refreshByUsername(userService.selectById(ur.getN_userId()).getC_username());
			if (flag) {
				msg = "添加成功！";
			} else {
				msg = "添加失败！";
			}
		}else{
			flag = false;
			msg = "添加失败，该用户已经存在角色！";
		}
		return new Result<>(flag,msg,0,null);
	}

	@PutMapping
	public Result<String> update(@ModelAttribute("ur") User_Role_Ca ur) {
		ur.setN_userId(Long.valueOf(ur.getC_username()));
		ur.setN_roleId(Long.valueOf(ur.getC_roleName()));
		Map<String, Object> map = new HashMap<String, Object>();
		User_Role_Ca user = urService.selectOne(new EntityWrapper<User_Role_Ca>().eq("n_userId", ur.getN_userId()));
		boolean flag;
		if(null == user){
			flag = urService.updateById(ur);
			//刷新缓存
			userService.refreshByUsername(userService.selectById(ur.getN_userId()).getC_username());
		}else{
			user.setN_roleId(ur.getN_roleId());
			flag = urService.updateById(user);
		}
		return new Result<>(flag,flag ? "更新成功！":"更新失败！",0,null);
	}

	@DeleteMapping("/{id}")
	public Result<String> delete(@PathVariable("id")Long id) {
		User_Role_Ca user = urService.selectById(id);

		boolean flag = urService.deleteById(id);
		//刷新缓存
		userService.refreshByUsername(userService.selectById(user.getN_userId()).getC_username());
		return new Result<>(flag,flag ? "删除成功！":"删除失败！",0,null);
	}

	@GetMapping
	public Result<List<User_Role_Ca>> findAll(@ModelAttribute("page")Page page) {
		List<User_Role_Ca> list = urService.findAll(page);
		Map<String, Object> map = new HashMap<String, Object>();
		int sum = urService.findAllCount(page);
		return new Result<>(true,null,sum,list);
	}

	@GetMapping("/search")
	public Result<List<User_Role_Ca>> search(
			@ModelAttribute("ur") User_Role_Ca ur,
			@ModelAttribute("page") Page page) {
		ur.setN_userId(Long.valueOf(" ".equals(ur.getC_username()) ? "0" : ur.getC_username()));
		ur.setN_roleId(Long.valueOf(" ".equals(ur.getC_roleName()) ? "0" : ur.getC_roleName()));
		Map<String, Object> map = new HashMap<String, Object>();
		List<User_Role_Ca> list = urService.searchAll(
				ur, page);
		int sum = urService.searchAllCount(ur);
		return new Result<>(true,null,sum,list);
	}

}
