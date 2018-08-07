<template>
  <main class="main">
    <Header></Header>
    <Navigation></Navigation>
    <keep-alive>
      <router-view></router-view>
    </keep-alive>
  </main>
</template>

<script type="text/ecmascript-6">
import { moment } from "moment";
import Header from "@/components/common/Header";
import Navigation from "@/components/common/Navigation";

export default {
  components: {
    Header,
    Navigation
  },
  mounted() {
    this.setTheme();

    this.init();
  },
  methods:{
    setTheme(){
      let theme = this.$storage.get(this.$const.keys.theme)
      if(theme) {
        $("body").attr("data-sa-theme", theme)
      }
    },
    init() {
      $(window).on("scroll", () => {
        $(window).scrollTop() >= 20
          ? $(".header").addClass("header--scrolled")
          : $(".header").removeClass("header--scrolled");
      }),
      $(document).ready(function() {
        if ($(".clock")[0]) {
          var a = new Date();
          a.setDate(a.getDate()),
            setInterval(function() {
              var a = new Date().getSeconds();
              $(".time__sec").html((a < 10 ? "0" : "") + a);
            }, 1e3),
            setInterval(function() {
              var a = new Date().getMinutes();
              $(".time__min").html((a < 10 ? "0" : "") + a);
            }, 1e3),
            setInterval(function() {
              var a = new Date().getHours();
              $(".time__hours").html((a < 10 ? "0" : "") + a);
            }, 1e3);
        }
    
          $("body").on("click", ".navigation__sub > a", function(a) {
            a.preventDefault(),
              $(this)
                .parent()
                .toggleClass("navigation__sub--toggled"),
              $(this)
                .next("ul")
                .slideToggle(250);
          }),
          $(".form-group--float")[0] &&
            ($(".form-group--float").each(function() {
              0 ==
                !$(this)
                  .find(".form-control")
                  .val().length &&
                $(this)
                  .find(".form-control")
                  .addClass("form-control--active");
            }),
            $("body").on(
              "blur",
              ".form-group--float .form-control",
              function() {
                0 == $(this).val().length
                  ? $(this).removeClass("form-control--active")
                  : $(this).addClass("form-control--active");
              }
            )),
          $("body").on("click", ".dropdown-menu--active", function(a) {
            a.stopPropagation();
          });
      }),
      $("#dropzone-upload")[0] && (Dropzone.autoDiscover = !1),
      $(document).ready(function() {
        if (
          ($(".textarea-autosize")[0] && autosize($(".textarea-autosize")),
          $("input-mask")[0] && $(".input-mask").mask(),
          $("select.select2")[0])
        ) {
          var a = $(".select2-parent")[0] ? $(".select2-parent") : $("body");
          $("select.select2").select2({
            dropdownAutoWidth: !0,
            width: "100%",
            dropdownParent: a
          });
        }
        if (
          ($("#dropzone-upload")[0] &&
            $("#dropzone-upload").dropzone({
              url: "/file/post",
              addRemoveLinks: !0
            }),
          $(".datetime-picker")[0] &&
            $(".datetime-picker").flatpickr({
              enableTime: !0,
              nextArrow: '<i class="zmdi zmdi-long-arrow-right" />',
              prevArrow: '<i class="zmdi zmdi-long-arrow-left" />'
            }),
          $(".date-picker")[0] &&
            $(".date-picker").flatpickr({
              enableTime: !1,
              nextArrow: '<i class="zmdi zmdi-long-arrow-right" />',
              prevArrow: '<i class="zmdi zmdi-long-arrow-left" />'
            }),
          $(".time-picker")[0] &&
            $(".time-picker").flatpickr({ noCalendar: !0, enableTime: !0 }),
          $("#input-slider")[0])
        ) {
          var b = document.getElementById("input-slider");
          noUiSlider.create(b, {
            start: [20],
            connect: "lower",
            range: { min: 0, max: 100 }
          }),
            b.noUiSlider.on("update", function(a, b) {
              document.getElementById("input-slider-value").value = a[b];
            });
        }
        if ($("#input-slider-range")[0]) {
          var c = document.getElementById("input-slider-range"),
            d = document.getElementById("input-slider-range-value-1"),
            e = document.getElementById("input-slider-range-value-2"),
            f = [d, e];
          noUiSlider.create(c, {
            start: [20, 80],
            connect: !0,
            range: { min: 0, max: 100 }
          }),
            c.noUiSlider.on("update", function(a, b) {
              f[b].value = a[b];
            });
        }
        if ($(".input-slider")[0])
          for (
            var g = document.getElementsByClassName("input-slider"), h = 0;
            h < g.length;
            h++
          )
            noUiSlider.create(g[h], {
              start: [20],
              connect: "lower",
              range: { min: 0, max: 100 }
            });
        if (
          ($(".color-picker")[0] &&
            ($(".color-picker__value").colorpicker(),
            $("body").on("change", ".color-picker__value", function() {
              $(this)
                .closest(".color-picker")
                .find(".color-picker__preview")
                .css("backgroundColor", $(this).val());
            })),
          $(".wysiwyg-editor")[0] &&
            $(".wysiwyg-editor").trumbowyg({ autogrow: !0 }),
          $(".lightbox")[0] && $(".lightbox").lightGallery({ enableTouch: !0 }),
          $('[data-toggle="popover"]')[0] &&
            $('[data-toggle="popover"]').popover(),
          $('[data-toggle="tooltip"]')[0] &&
            $('[data-toggle="tooltip"]').tooltip(),
          $(".widget-calendar__body")[0])
        ) {
          $(".widget-calendar__body").fullCalendar({
            contentHeight: "auto",
            theme: !1,
            buttonIcons: {
              prev: " zmdi zmdi-long-arrow-left",
              next: " zmdi zmdi-long-arrow-right"
            },
            header: { right: "next", center: "title, ", left: "prev" },
            defaultDate: "2016-08-12",
            editable: !0,
            events: [
              { title: "Dolor Pellentesque", start: "2016-08-01" },
              {
                title: "Purus Nibh",
                start: "2016-08-07"
              },
              { title: "Amet Condimentum", start: "2016-08-09" },
              { title: "Tellus", start: "2016-08-12" },
              {
                title: "Vestibulum",
                start: "2016-08-18"
              },
              { title: "Ipsum", start: "2016-08-24" },
              { title: "Fringilla Sit", start: "2016-08-27" },
              {
                title: "Amet Pharetra",
                url: "http://google.com/",
                start: "2016-08-30"
              }
            ]
          });
        }
        if ($(".notes__body")[0]) {
          var k;
          $(".notes__body").each(function(a, b) {
            (k = $(this)
              .prev()
              .is(".notes__title")
              ? 4
              : 6),
              $clamp(b, { clamp: k });
          });
        }
        $(".scrollbar-inner")[0] &&
          $(".scrollbar-inner")
            .scrollbar()
            .scrollLock();
        var l = [
            {
              name: "node1",
              children: [
                {
                  name: "node1_1",
                  children: [
                    { name: "node1_1_1" },
                    { name: "node1_1_2" },
                    { name: "node1_1_3" }
                  ]
                },
                { name: "node1_2" },
                { name: "node1_3" }
              ]
            },
            {
              name: "node2",
              children: [
                { name: "node2_1" },
                { name: "node2_2" },
                { name: "node2_3" }
              ]
            },
            {
              name: "node3",
              children: [
                { name: "node3_1" },
                { name: "node3_2" },
                { name: "node3_3" }
              ]
            }
          ],
          m = [
            {
              name: "node1",
              children: [
                { name: "node1_1" },
                { name: "node1_2" },
                { name: "node1_3" }
              ]
            },
            {
              name: "node2",
              children: [
                { name: "node2_1" },
                { name: "node2_2" },
                { name: "node2_3" }
              ]
            }
          ],
          n = [
            {
              label: "node1",
              children: [
                { name: '<a href="example1.html">node1_1</a>' },
                { name: '<a href="example2.html">node1_2</a>' },
                '<a href="example3.html">Example </a>'
              ]
            }
          ];
        $(".treeview")[0] &&
          $(".treeview").tree({
            data: l,
            closedIcon: $('<i class="zmdi zmdi-plus"></i>'),
            openedIcon: $('<i class="zmdi zmdi-minus"></i>')
          }),
          $(".treeview-expanded")[0] &&
            $(".treeview-expanded").tree({
              data: m,
              autoOpen: !0,
              closedIcon: $('<i class="zmdi zmdi-plus"></i>'),
              openedIcon: $('<i class="zmdi zmdi-minus"></i>')
            }),
          $(".treeview-drag")[0] &&
            $(".treeview-drag").tree({
              data: m,
              dragAndDrop: !0,
              autoOpen: !0,
              closedIcon: $('<i class="zmdi zmdi-plus"></i>'),
              openedIcon: $('<i class="zmdi zmdi-minus"></i>')
            }),
          $(".treeview-drag")[0] &&
            $(".treeview-drag").tree({
              data: m,
              dragAndDrop: !0,
              autoOpen: !0,
              closedIcon: $('<i class="zmdi zmdi-plus"></i>'),
              openedIcon: $('<i class="zmdi zmdi-minus"></i>')
            }),
          $(".treeview-escape")[0] &&
            $(".treeview-escape").tree({
              data: n,
              autoEscape: !1,
              autoOpen: !0,
              closedIcon: $('<i class="zmdi zmdi-plus"></i>'),
              openedIcon: $('<i class="zmdi zmdi-minus"></i>')
            }),
          $(".rating")[0] &&
            $(".rating").each(function() {
              var a = $(this).data("rating");
              $(this).rateYo({
                rating: a,
                normalFill: "rgba(255,255,255,0.3)",
                ratedFill: "#ffc107"
              });
            }),
          $(".text-counter")[0] &&
            $(".text-counter").each(function() {
              var a = $(this).data("min-length") || 0,
                b = $(this).data("max-length");
              $(this).textcounter({
                min: a,
                max: b,
                countDown: !0,
                inputErrorClass: "is-invalid",
                counterErrorClass: "text-orange"
              });
            });
      }),
      $(document).ready(function() {
        function a(a) {
          a.requestFullscreen
            ? a.requestFullscreen()
            : a.mozRequestFullScreen
              ? a.mozRequestFullScreen()
              : a.webkitRequestFullscreen
                ? a.webkitRequestFullscreen()
                : a.msRequestFullscreen && a.msRequestFullscreen();
        }

        $("body").on("click", "[data-sa-action]", function(b) {
          b.preventDefault();
          var c = $(this),
            d = c.data("sa-action"),
            e = "";
          switch (d) {
            case "search-open":
              $(".search").addClass("search--toggled");
              break;
            case "search-close":
              $(".search").removeClass("search--toggled");
              break;
            case "aside-open":
              (e = c.data("sa-target")),
                c.addClass("toggled"),
                $("body").addClass("aside-toggled"),
                $(e).addClass("toggled"),
                $(".content, .header").append(
                  '<div class="sa-backdrop" data-sa-action="aside-close" data-sa-target=' +
                    e +
                    " />"
                );
              break;
            case "aside-close":
              (e = c.data("sa-target")),
                $("body").removeClass("aside-toggled"),
                $('[data-sa-action="aside-open"], ' + e).removeClass("toggled"),
                $(".content, .header")
                  .find(".sa-backdrop")
                  .remove();
              break;
            case "fullscreen":
              a(document.documentElement);
              break;
            case "print":
              window.print();
              break;
            case "login-switch":
              (e = c.data("sa-target")),
                $(".login__block").removeClass("active"),
                $(e).addClass("active");
              break;
            case "notifications-clear":
              b.stopPropagation();
              var f = $(".top-nav__notifications .listview__item"),
                g = f.length,
                h = 0;
              c.fadeOut(),
                f.each(function() {
                  var a = $(this);
                  setTimeout(function() {
                    a.addClass("animated fadeOutRight");
                  }, (h += 150));
                }),
                setTimeout(function() {
                  f.remove(),
                    $(".top-nav__notifications").addClass(
                      "top-nav__notifications--cleared"
                    );
                }, 180 * g);
              break;
            case "toolbar-search-open":
              $(this)
                .closest(".toolbar")
                .find(".toolbar__search")
                .fadeIn(200),
                $(this)
                  .closest(".toolbar")
                  .find(".toolbar__search input")
                  .focus();
              break;
            case "toolbar-search-close":
              $(this)
                .closest(".toolbar")
                .find(".toolbar__search input")
                .val(""),
                $(this)
                  .closest(".toolbar")
                  .find(".toolbar__search")
                  .fadeOut(200);
          }
        });
      });
    }
    
  }
};
</script>

<style>
@import "../static/css/app.min.css"
</style>
