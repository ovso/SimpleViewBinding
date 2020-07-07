# Simple ViewBinding

[뷰바인딩](https://developer.android.com/topic/libraries/view-binding?hl=en)은 [데이터바인딩](https://developer.android.com/topic/libraries/data-binding?hl=en)과는 다르게 부모클래스를 만들기 어렵다. 데이터바인딩은 [DataBindingUtil](https://developer.android.com/reference/android/databinding/DataBindingUtil)과 같은 API가 있기 때문에 제네릭을 통한 상속 형태의 구현이 가능하지만, 뷰바인딩은 데이터바인딩처럼 별도의 API가 없음으로 상속 형태의 구현이 어려운 것이다. 

굳이, 상속 형태로 구현하려면 부모클래스에 ActivityMainBinding 관련 정보를 넘기고 부모클래스에서 직접 Reflection를 다루는 형태로 구현해야 한다.

때문에, 심플하게 사용할 수 있도록 뷰바인딩을 구현하려면 코틀린의 [Delegate](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/)와 [확장함수](https://kotlinlang.org/docs/reference/extensions.html#extensions)를 사용해 구현할 수 있다.

## Fragment

### 구현

```kotlin
class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)
```

### 사용

```kotlin
class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding by viewBinding(MainFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.message.setOnClickListener {
            Toast.makeText(requireContext(), "Fragment!!", Toast.LENGTH_SHORT).show()
        }
    }
}
```



## Activity

### 구현

```kotlin
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }
```

### 사용

```kotlin
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(MainActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnMain.setOnClickListener {
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show()
        }
    }
}
```





- [Simple one-liner ViewBinding in Fragments and Activities with Kotlin](https://medium.com/@Zhuinden/simple-one-liner-viewbinding-in-fragments-and-activities-with-kotlin-961430c6c07c)

