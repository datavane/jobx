export default {
    install(Vue, options) {
        Vue.prototype.$dataTable = function (options) {
            $("#data-table").DataTable({
                autoWidth: !1,
                responsive: !0,
                lengthMenu: [
                  [15, 30, 45, -1],
                  ["15 Rows", "30 Rows", "45 Rows", "Everything"]
                ],
                language: { 
                  searchPlaceholder: "Search for records...",
                  sInfoEmpty: "没有数据",
                  sZeroRecords: "没有查找到满足条件的数据",
                  sInfo: "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
                  sLengthMenu: "每页显示 _MENU_ 条记录",
                  sInfoFiltered: "(从 _MAX_ 条数据中检索)"
                }
              })
        }
    }
}