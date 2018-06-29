Number.prototype.getChar = function(){
    var arrays = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");
    return arrays[this];
}

function Validata() {

    this.contextPath = arguments[0]||'';
    this.jobId = arguments[1]||null;

    var self = this;

    this.validata =  {

        status: true,

        jobNameRemote:false,

        cronExpRemote:false,

        init: function () {
            this.status = true;
            this.jobNameRemote = false;
            this.cronExpRemote = false;
        },

        jobName: function () {
            var prefix = arguments[0] || "";
            var _jobName = $("#jobName" + prefix).val();
            if (!_jobName) {
                jobx.tipError("#jobName" + prefix, "必填项,作业名称不能为空");
                this.status = false;
            } else {
                if (_jobName.length < 4 || _jobName.length > 51) {
                    jobx.tipError("#jobName" + prefix, "作业名称不能小于4个字符并且不能超过50个字符!");
                    this.status = false;
                } else {
                    var _this = this;
                    ajax({
                        type: "POST",
                        url: self.contextPath+"/job/checkname.do",
                        data: {
                        "jobId":self.jobId,
                            "name": _jobName,
                            "agentId": $("#agentId").val()
                        }
                    },function (data) {
                        _this.jobNameRemote = true;
                        if (!data.status) {
                            jobx.tipError("#jobName" + prefix, "作业名称已存在!");
                            _this.status = false;
                        } else {
                            jobx.tipOk("#jobName" + prefix);
                        }
                    },function () {
                        _this.jobNameRemote = true;
                        _this.status = false;
                        jobx.tipError("#jobName" + prefix, "网络请求错误,请重试!");
                    });
                }
            }
        },

        cronExp: function () {

            var cronExp = $("#cronExp").val();

            if (cronExp.trim().length === 0) {
                jobx.tipError($("#cronExp"), "时间规则不能为空,请填写时间规则");
                this.status = false;
                return;
            }

            if(cronExp.trim().split(' ').length < 7){
                jobx.tipError($("#expTip"), "时间规则语法错误!");
                this.status = false;
                return;
            }

            var _this = this;
            ajax({
                type: "POST",
                url: self.contextPath+"/verify/exp.do",
                data: {
                    "cronExp": cronExp
                }
            },function (data) {
                _this.cronExpRemote = true;
                if (data.status) {
                    jobx.tipOk($("#expTip"));
                } else {
                    $("#expTip").css("visibility","visible");
                    jobx.tipError($("#expTip"), "时间规则语法错误!");
                    _this.status = false;
                }
            },function () {
                _this.cronExpRemote = true;
                jobx.tipError($("#expTip"), "网络请求错误,请重试!");
                _this.status = false;
            })
        },

        command: function () {
            var prefix = arguments[0] || "";
            if ($("#cmd" + prefix).val().length == 0) {
                jobx.tipError("#cmd" + prefix, "执行命令不能为空,请填写执行命令");
                this.status = false;
            } else {
                jobx.tipOk("#cmd" + prefix);
            }
        },

        platform:function() {
            var platform = $("#agentId").find("option:selected").attr("platform");
            if (platform==1) {
                var execUser = $("#execUser").val();
                if (!execUser) {
                    jobx.tipError("#execUser","请选择执行身份");
                    this.status = false;
                }
            }
        },

        agentId:function(){
            if ($("#agentId").length>0) {
                var agentId = $("#agentId").val();
                if (!agentId) {
                    jobx.tipError("#agentId", "请选择执行器");
                    this.status = false;
                }
            }
        },

        successExit: function () {
            var prefix = arguments[0] || "";
            var successExit = $("#successExit" + prefix).val();
            if (successExit.length == 0) {
                jobx.tipError("#successExit" + prefix, "自定义成功标识不能为空");
                this.status = false;
            } else if (isNaN(successExit)) {
                jobx.tipError("#successExit" + prefix, "自定义成功标识必须为数字");
                this.status = false;
            } else {
                jobx.tipOk("#successExit" + prefix);
            }
        },

        runCount: function () {
            var prefix = arguments[0] || "";
            var redo = prefix ? $("#itemRedo").val() : $('input[type="radio"][name="redo"]:checked').val();
            var reg = /^[0-9]*[1-9][0-9]*$/;
            if (redo == 1) {
                var _runCount = $("#runCount" + prefix).val();
                if (!_runCount) {
                    jobx.tipError("#runCount" + prefix, "请填写重跑次数!");
                    this.status = false;
                } else if (!reg.test(_runCount)) {
                    jobx.tipError("#runCount" + prefix, "截止重跑次数必须为正整数!");
                    this.status = false;
                } else {
                    jobx.tipOk("#runCount" + prefix);
                }
            }
        },

        mobile: function () {
            var mobile = $("#mobile").val();
            if (!mobile) {
                jobx.tipError("#mobile", "请填写手机号码!");
                this.status = false;
                return;
            }
            var mobs = mobile.split(",");
            var verify = true;
            for (var i=0;i<mobs.length;i++) {
                if (!jobx.testMobile(mobs[i])) {
                    this.status = false;
                    verify = false;
                    jobx.tipError("#mobile", "请填写正确的手机号码!");
                    break;
                }
            }
            if (verify) {
                jobx.tipOk("#mobile");
            }
        },

        email: function () {
            var emails = $("#email").val();
            if (!emails) {
                jobx.tipError("#email", "请填写邮箱地址!");
                this.status = false;
                return;
            }
            var emas = emails.split(",");
            var verify = true;
            for ( var i=0;i<emas.length;i++ ) {
                if (!jobx.testEmail(emas[i])) {
                    jobx.tipError("#email", "请填写正确的邮箱地址!");
                    this.status = false;
                    verify = false;
                    break;
                }
            }
            if (verify) {
                jobx.tipOk("#email");
            }
        },

        timeout: function () {
            var prefix = arguments[0] || "";
            var timeout = $("#timeout" + prefix).val();
            if (timeout.length > 0) {
                if (isNaN(timeout) || parseInt(timeout) < 0) {
                    jobx.tipError("#timeout" + prefix, "超时时间必须为正整数,请填写正确的超时时间!");
                    this.status = false;
                } else {
                    jobx.tipOk("#timeout" + prefix);
                }
            } else {
                this.status = false;
                jobx.tipError("#timeout" + prefix, "超时时间不能为空,请填写(0:忽略超时时间,分钟为单位!");
            }
        },

        warning: function () {
            var _warning = $('input[type="radio"][name="warning"]:checked').val();
            if (_warning == 1) {
                this.mobile();
                this.email();
            }
        },

        verify: function () {
            this.init();
            this.jobName();
            this.cronExp();
            this.runCount();
            this.timeout();
            this.warning();
            var jobType = $("#jobType").val();
            if (jobType == 0) {
                this.command();
                this.platform();
                this.agentId();
                this.successExit();
            } else {

            }
            return this.status;
        }
    };

    this.flowJob = {

        jobFlagNum:0,

        tipDefault: function () {
            jobx.tipDefault("#jobName1");
            jobx.tipDefault("#cmd1");
            jobx.tipDefault("#successExit1");
            jobx.tipDefault("#timeout1");
            jobx.tipDefault("#runCount1");
            $("#jobModal").find(".ok").remove();
        },

        add: function () {
            $("#subForm")[0].reset();
            self.toggle.redo(1);
            this.tipDefault();
            $("#subTitle").html("添加作业依赖").attr("action", "add");
        },

        edit: function (id) {
            $("#jobModal").find(".ok").remove();
            $("#jobModal").find(".tips").css("visibility","visibility");
            $("#subTitle").html("编辑作业依赖").attr("action", "edit").attr("tid", id);
            $("#" + id).find("input").each(function (index, element) {
                if ($(element).attr("name") == "child.jobName") {
                    $("#jobName1").val(unEscapeHtml($(element).val()));
                }
                if ($(element).attr("name") == "child.agentId") {
                    $("#agentId1").val($(element).val());
                }
                if ($(element).attr("name") == "child.command") {
                    $("#cmd1").val(passBase64($(element).val()));
                }

                if ($(element).attr("name") == "child.successExit") {
                    $("#successExit1").val($(element).val());
                }

                if ($(element).attr("name") == "child.redo") {
                    self.toggle.redo($("#itemRedo").val() || $(element).val());
                }

                if ($(element).attr("name") == "child.runCount") {
                    $("#runCount1").val($(element).val());
                }

                if ($(element).attr("name") == "child.timeout") {
                    $("#timeout1").val($(element).val());
                }

                if ($(element).attr("name") == "child.comment") {
                    $("#comment1").val(unEscapeHtml($(element).val()));
                }
            });
        },

        remove: function (node,num) {
            swal({
                title: "",
                text: "您确定要删除该作业吗？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: true,
                confirmButtonText: "删除"
            },function () {
                alertMsg("删除作业成功");
                $(node).parent().slideUp(300, function () {
                    this.remove();
                    self.flowJob.graphH();
                    var deps = $(".depen-input").val();
                    if (deps.length == 0) return;

                    var char = num.getChar();
                    var reg1 = char+">";
                    var reg2 = char+",";
                    var reg3 = ","+char;
                    var reg4 = ">"+char;

                    deps = deps.replace(reg1,"").replace(reg1.toLowerCase(),"")
                        .replace(reg2,"").replace(reg2.toLowerCase(),"")
                        .replace(reg3,"").replace(reg3.toLowerCase(),"")
                        .replace(reg4,"").replace(reg4.toLowerCase(),"")
                    $(".depen-input").val(deps);
                    graph();
                });
            });
        },

        close: function () {
            $("#subForm")[0].reset();
            $('#jobModal').modal('hide');
        },

        graphH:function() {
            var children = $("#flowJobDiv").find(".jobnum").length;
            var graphH = children * 35;
            $(".graph").css({
                "margin-top":"-"+(230+graphH+children)+ "px",
                "height":240+graphH+"px"
            });
        },

        verify: function () {
            self.validata.init();
            self.validata.jobName("1");
            self.validata.command("1");
            self.validata.successExit("1");
            self.validata.timeout("1");
            self.validata.runCount("1");

            var valId = setInterval(function () {
                if (self.validata.jobNameRemote ) {
                    window.clearInterval(valId);
                    if (self.validata.status) {
                        //添加
                        var _jobName = $("#jobName1").val();
                        if ($("#subTitle").attr("action") === "add") {
                            var timestamp = Date.parse(new Date());
                            var currNum = self.flowJob.jobFlagNum++;
                            var currJobNum = currNum.getChar();
                            var addHtml =
                                "<li id='" + timestamp + "'>" +
                                "<input type='hidden' name='child.createType' value='2'>" +
                                "<input type='hidden' name='child.jobId' value=''>" +
                                "<input type='hidden' name='child.jobName' num='"+currNum+"' value='" + escapeHtml(_jobName) + "'>" +
                                "<input type='hidden' name='child.agentId' value='" + $("#agentId1").val() + "'>" +
                                "<input type='hidden' name='child.command' value='" + toBase64($("#cmd1").val()) + "'>" +
                                "<input type='hidden' name='child.redo' value='" + $('#itemRedo').val() + "'>" +
                                "<input type='hidden' name='child.runCount' value='" + $("#runCount1").val() + "'>" +
                                "<input type='hidden' name='child.timeout' value='" + $("#timeout1").val() + "'>" +
                                "<input type='hidden' name='child.successExit' value='" + $("#successExit1").val() + "'>" +
                                "<input type='hidden' name='child.comment' value='" + escapeHtml($("#comment1").val()) + "'>" +
                                "<span id='name_" + timestamp + "'><div class='circle'></div><span class='jobnum' num='"+currNum+"' name='"+escapeHtml(_jobName)+"'>"+currJobNum+"</span>"  + escapeHtml(_jobName) + "</span>" +
                                "<span class='delSubJob' onclick='jobxValidata.flowJob.remove(this,"+currNum+")' style='float:right; margin-right: 5px;'>" +
                                "   <i class='glyphicon glyphicon-trash' title='删除'></i>" +
                                "</span>" +
                                "<span onclick='jobxValidata.flowJob.edit(\"" + timestamp + "\")' style='float:right; margin-right: 5px;'>" +
                                "   <a data-toggle='modal' href='#jobModal' title='编辑'>" +
                                "       <i class='glyphicon glyphicon-pencil'></i>&nbsp;&nbsp;" +
                                "   </a>" +
                                "</span>" +
                                "</li>";
                            $("#flowJobDiv").show().append($(addHtml));
                            self.flowJob.graphH();
                        } else if ($("#subTitle").attr("action") == "edit") {//编辑
                            var id = $("#subTitle").attr("tid");
                            var currNum = 0;
                            $("#" + id).find("input").each(function (index, element) {
                                if ($(element).attr("name") == "child.jobName") {
                                    $(element).attr("value", _jobName);
                                    currNum =  parseInt($(element).attr("num"));
                                }

                                if ($(element).attr("name") == "child.redo") {
                                    $(element).attr("value", $('#itemRedo').val());
                                }

                                if ($(element).attr("name") == "child.runCount") {
                                    $(element).attr("value", $("#runCount1").val());
                                }

                                if ($(element).attr("name") == "child.successExit") {
                                    $(element).attr("value", $("#successExit1").val());
                                }

                                if ($(element).attr("name") == "child.agentId") {
                                    $(element).attr("value", $("#agentId1").val());
                                }
                                if ($(element).attr("name") == "child.command") {
                                    $(element).attr("value", toBase64($("#cmd1").val()));
                                }

                                if ($(element).attr("name") == "child.timeout") {
                                    $(element).attr("value", $("#timeout1").val());
                                }

                                if ($(element).attr("name") == "child.comment") {
                                    $(element).attr("value", $("#comment1").val());
                                }
                            });
                            var numHtml = "<div class='circle'></div><span class='jobnum' num='"+currNum+"' name='"+_jobName+"'>"+currNum.getChar()+"</span>";
                            $("#name_" + id).html(numHtml+escapeHtml(_jobName));
                            graph();
                        }
                        self.flowJob.close();
                    }
                }
            },10);
        }
    };

    this.toggle = {
        redo: function (_toggle) {
            $("#itemRedo").val(_toggle);
            if (_toggle == 1) {
                $(".countDiv1").show();
                $("#redo1").prop("checked", true);
                $("#redo1").parent().removeClass("checked").addClass("checked");
                $("#redo1").parent().attr("aria-checked", true);
                $("#redo1").parent().prop("onclick", "showContact()");
                $("#redo0").parent().removeClass("checked");
                $("#redo0").parent().attr("aria-checked", false);
            } else {
                $(".countDiv1").hide();
                $("#redo0").prop("checked", true);
                $("#redo0").parent().removeClass("checked").addClass("checked");
                $("#redo0").parent().attr("aria-checked", true);
                $("#redo1").parent().removeClass("checked");
                $("#redo1").parent().attr("aria-checked", false);
            }
        },
        count: function (_toggle) {
            if (_toggle) {
                $("#redo").val(1);
                $(".countDiv").show()
            } else {
                $("#redo").val(0);
                $(".countDiv").hide();
            }
        },
        contact: function (_toggle) {
            if (_toggle) {
                $(".contact").show()
            } else {
                $(".contact").hide();
            }
        }
    };

    this.ready();
}

Validata.prototype.ready = function () {

    var _this = this;

    $("#redo01").next().click(function () {
        _this.toggle.count(true)
    });
    $("#redo01").parent().parent().click(function () {
        _this.toggle.count(true)
    });
    $("#redo00").next().click(function () {
        _this.toggle.count(false)
    });
    $("#redo00").parent().parent().click(function () {
        _this.toggle.count(false)
    });

    $("#redo1").next().click(function () {
        _this.toggle.redo(1);
    });
    $("#redo1").parent().parent().click(function () {
        _this.toggle.redo(1);
    })
    $("#redo0").next().click(function () {
        _this.toggle.redo(0);
    });
    $("#redo0").parent().parent().click(function () {
        _this.toggle.redo(0);
    });


    $("#warning0").next().click(function () {
        _this.toggle.contact(false);
    });
    $("#warning0").parent().parent().click(function () {
        _this.toggle.contact(false);
    });

    $("#warning1").next().click(function () {
        _this.toggle.contact(true);
    });
    $("#warning1").parent().parent().click(function () {
        _this.toggle.contact(true);
    });

    var redo = $('input[type="radio"][name="redo"]:checked').val();
    _this.toggle.count(redo == 1);

    var warning = $('input[type="radio"][name="warning"]:checked').val();
    _this.toggle.contact(warning == 1);

    $("#save-btn").click(function () {
        _this.validata.verify();
        if(_this.validata.status){
            var valId = setInterval(function () {
                if (_this.validata.jobNameRemote ) {
                    var cleanFlag = false;
                    if(_this.validata.cronExpRemote){
                        cleanFlag = true;
                    }
                    if(cleanFlag){
                        window.clearInterval(valId);
                        if(_this.validata.status) {
                            var cmd = $("#cmd").val();
                            $("#command").val(toBase64(cmd));
                            $("#jobform").submit();
                        }
                    }
                }
            },10);
        }
    });

    $("#flowJob-btn").click(function () {
        _this.flowJob.verify();
    });

    $("#jobName").blur(function () {
        _this.validata.jobName();
    }).focus(function () {
        jobx.tipDefault("#jobName");
    });

    $("#cronExp").blur(function () {
        _this.validata.cronExp();
    }).dblclick(function () {
        new CronInput(_this.contextPath,function (exp) {
           $("#cronExp").val(exp);
        });
        $("#expTip").css("visibility","visible").html('请采用quartz框架的时间格式表达式,如 0 0 10 L * ?');
    }).bind('input propertychange change', function() {
        _this.validata.cronExp();
    });


    $("#jobName1").blur(function () {
        _this.validata.jobName("1");
    }).focus(function () {
        jobx.tipDefault("#jobName1");
    });

    $("#cmd").blur(function () {
        _this.validata.command();
    }).focus(function () {
        jobx.tipDefault("#cmd");
    });
    $("#cmd1").blur(function () {
        _this.validata.command("1");
    }).focus(function () {
        jobx.tipDefault("#cmd1");
    });

    $("#runCount").blur(function () {
        _this.validata.runCount();
    }).focus(function () {
        jobx.tipDefault("#runCount");
    });
    $("#runCount1").blur(function () {
        _this.validata.runCount("1");
    }).focus(function () {
        jobx.tipDefault("#runCount1");
    });

    $("#successExit").blur(function () {
        _this.validata.successExit();
    }).focus(function () {
        jobx.tipDefault("#successExit");
    });
    $("#successExit1").blur(function () {
        _this.validata.successExit("1");
    }).focus(function () {
        jobx.tipDefault("#successExit1");
    });

    $("#timeout").blur(function () {
        _this.validata.timeout();
    }).focus(function () {
        jobx.tipDefault("#timeout");
    });

    $("#timeout1").blur(function () {
        _this.validata.timeout("1");
    }).focus(function () {
        jobx.tipDefault("#timeout1");
    });

    $("#mobile").blur(function () {
        _this.validata.mobile();
    }).focus(function () {
        jobx.tipDefault("#mobile");
    });

    $("#email").blur(function () {
        _this.validata.email();
    }).focus(function () {
        jobx.tipDefault("#email");
    });
};
