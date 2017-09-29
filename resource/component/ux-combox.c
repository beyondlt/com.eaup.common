<template id="ux-picker">
    <div >
        <el-input ref="textInput" :icon="icon" :on-icon-click="_comboxClick" v-model="text" :readonly="true" @blur="validate"></el-input>
        <input ref="valueInput" type="text" style="display: none" v-model="value"/>
        <div v-if="$slots.context">
            <slot name="context"></slot>
        </div>
    </div>
</template>
<script>
    Vue.component('ux-picker', {
        template: '#ux-picker',
        data: function () {
            return {
                icon: 'caret-bottom'
            }
        },

        props: {
            value: [String, Number],
            text: String,
            validateEvent: {
                type: Boolean,
                default: true
            }
        },

        mounted: function () {
            if (this.$slots.context) {
                this.$slots.context[0].elm.style.visibility = "hidden"
                this.$slots.context[0].elm.style['background-color'] = "#fff"
                this.$slots.context[0].elm.style['display'] = "inline-block"
                this.$slots.context[0].elm.style['float'] = "left"
                this.$slots.context[0].elm.style['z-index'] = 900
                this.$slots.context[0].elm.style['position'] = "absolute"
                var width=$(this.$refs.textInput.$el).width()

                $( this.$slots.context[0]).width(width)


            }
        },
        watch:{
            value:function(){
                this.validate();
            }
        },
        methods: {
            validate:function(){
                this.$emit('blur', event);
                if (this.validateEvent) {
                    this.dispatch('ElFormItem', 'el.form.blur', [this.value]);

                }
            },
            dispatch(componentName, eventName, params) {
                var parent = this.$parent || this.$root;
                var name = parent.$options.componentName;

                while (parent && (!name || name !== componentName)) {
                    parent = parent.$parent;

                    if (parent) {
                        name = parent.$options.componentName;
                    }
                }
                if (parent) {
                    parent.$emit.apply(parent, [eventName].concat(params));
                }
            },

            setValue: function (value, text) {

                this.value = value;
                if (text) {
                    this.text = text;
                } else {
                    this.value = value;
                }
            },
            _comboxClick: function () {
                if (this.icon == "caret-bottom") {
                    this.expand()
                } else {
                    this.collapse()
                }
            },
            expand: function () {
                this.$slots.context[0].elm.style.visibility = 'visible';
                this.icon = "caret-top"
            },
            collapse: function () {
                this.$slots.context[0].elm.style.visibility = 'hidden';
                this.icon = "caret-bottom"
                this.validate();
            }

        }

    })
</script>
